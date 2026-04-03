package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallAudioResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.enums.AudioStatus;
import SmartFoodStreet_Backend.repository.StallTranslationRepository;
import SmartFoodStreet_Backend.service.interfaces.IStallTranslation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StallTranslationService implements IStallTranslation {
    private final StallTranslationRepository repository;
    private final TtsService ttsService;
    private final CloudinaryService cloudinaryService;

    @Override
    public StallTranslationResponse create(StallTranslationRequest stallTranslationRequest) {
        boolean exists = repository
                .existsByStallIdAndLanguageCode(stallTranslationRequest.getStallId(), stallTranslationRequest.getLanguageCode());

        if (exists) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS);
        }

        StallTranslation stallTranslation = StallTranslation.builder()
                .stallId(stallTranslationRequest.getStallId())
                .languageCode(stallTranslationRequest.getLanguageCode())
                .name(stallTranslationRequest.getLanguageCode()+"_"+stallTranslationRequest.getStallId())
                .ttsScript(stallTranslationRequest.getTtsScript())
                .audioStatus(AudioStatus.PENDING)
                .build();

        repository.save(stallTranslation);

        return map(stallTranslation);
    }

    @Override
    public StallTranslationResponse update(Long id, StallTranslationRequest stallTranslationRequest) {

        StallTranslation stallTranslation = find(id);

        stallTranslation.setName(stallTranslationRequest.getLanguageCode()+"_"+stallTranslationRequest.getStallId());
        stallTranslation.setTtsScript(stallTranslationRequest.getTtsScript());

        // reset audio khi script thay đổi
        stallTranslation.setAudioStatus(AudioStatus.PENDING);
        stallTranslation.setAudioUrl(null);
        stallTranslation.setAudioHash(null);
        stallTranslation.setFileSize(0L);

        repository.save(stallTranslation);

        return map(stallTranslation);
    }

    @Override
    public StallTranslationResponse getById(Long id) {
        return map(find(id));
    }

    @Override
    public List<StallTranslationResponse> getByStall(Long stallId) {
        return repository.findByStallId(stallId)
                .stream().map(this::map).toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // =========================
    // CORE AUDIO FLOW
    // =========================

    @Override
    @Transactional
    public StallAudioResponse getAudio(Long stallId, String lang, String clientHash) {

        StallTranslation stallTranslation = (StallTranslation) repository
                .findByStallIdAndLanguageCode(stallId, lang)
                .orElseThrow(() -> new AppException(ErrorCode.STALL_TRANSLATION_NOT_FOUND));

        // COMPLETED
        if (stallTranslation.getAudioStatus() == AudioStatus.COMPLETED) {

            if (clientHash != null && clientHash.equals(stallTranslation.getAudioHash())) {
                return StallAudioResponse.builder()
                        .needDownload(false)
                        .message("Use local audio")
                        .build();
            }

            return StallAudioResponse.builder()
                    .needDownload(true)
                    .audioUrl(stallTranslation.getAudioUrl())
                    .fileSize(stallTranslation.getFileSize())
                    .audioHash(stallTranslation.getAudioHash())
                    .status(AudioStatus.COMPLETED)
                    .message("Generate audio successfully")
                    .build();
        }

        // PROCESSING
        if (stallTranslation.getAudioStatus() == AudioStatus.PROCESSING) {
            return StallAudioResponse.builder()
                    .status(AudioStatus.PROCESSING)
                    .message("Audio is generating")
                    .build();
        }

        // PENDING / ERROR
        if (stallTranslation.getAudioStatus() == AudioStatus.PENDING
                || stallTranslation.getAudioStatus() == AudioStatus.ERROR) {

            stallTranslation.setAudioStatus(AudioStatus.PROCESSING);
            repository.save(stallTranslation);

            generateAudioAsync(stallTranslation.getId());

            return StallAudioResponse.builder()
                    .status(AudioStatus.PROCESSING)
                    .message("Generating audio")
                    .build();
        }

        throw new AppException(ErrorCode.INVALID_REQUEST);
    }

    // =========================
    // ASYNC TTS
    // =========================

    @Async
    public void generateAudioAsync(Long id) {

        StallTranslation stallTranslation = repository.findById(id).orElseThrow();

        try {
            String text = stallTranslation.getTtsScript();

            byte[] audio = ttsService.generate(stallTranslation.getTtsScript(), stallTranslation.getLanguageCode());

            CloudinaryResponse upload = cloudinaryService.uploadAudio(audio, stallTranslation.getName());

            String hash = DigestUtils.md5DigestAsHex(audio);

            stallTranslation.setAudioUrl(upload.getUrl());
            stallTranslation.setFileSize(upload.getBytes());
            stallTranslation.setAudioHash(hash);
            stallTranslation.setAudioStatus(AudioStatus.COMPLETED);

        } catch (Exception e) {
            stallTranslation.setAudioStatus(AudioStatus.ERROR);
        }

        repository.save(stallTranslation);
    }

    // =========================
    // INTERNAL
    // =========================

    private StallTranslation find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private StallTranslationResponse map(StallTranslation stallTranslation) {
        return StallTranslationResponse.builder()
                .stallId(stallTranslation.getId())
                .stallId(stallTranslation.getStallId())
                .languageCode(stallTranslation.getLanguageCode())
                .name(stallTranslation.getName())
                .ttsScript(stallTranslation.getTtsScript())
                .audioUrl(stallTranslation.getAudioUrl())
                .fileSize(stallTranslation.getFileSize())
                .audioHash(stallTranslation.getAudioHash())
                .audioStatus(stallTranslation.getAudioStatus().name())
                .build();
    }
}