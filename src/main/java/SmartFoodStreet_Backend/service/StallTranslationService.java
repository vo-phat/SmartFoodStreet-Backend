package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallAudioResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.enums.AudioStatus;
import SmartFoodStreet_Backend.repository.StallTranslationRepository;
import SmartFoodStreet_Backend.service.interfaces.IStallTranslation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StallTranslationService implements IStallTranslation {

    private final StallTranslationRepository repository;
    private final AudioProcessorService audioProcessorService;

    @Override
    public StallTranslationResponse create(StallTranslationRequest request) {
        boolean exists = repository.existsByStallIdAndLanguageCode(request.getStallId(), request.getLanguageCode());

        if (exists) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS);
        }

        StallTranslation stallTranslation = StallTranslation.builder()
                .stallId(request.getStallId())
                .languageCode(request.getLanguageCode())
                .name(request.getLanguageCode() + "_" + request.getStallId())
                .ttsScript(request.getTtsScript())
                .audioStatus(AudioStatus.PENDING)
                .build();

        repository.save(stallTranslation);

        return map(stallTranslation);
    }

    @Override
    public StallTranslationResponse update(Long id, StallTranslationRequest request) {

        StallTranslation stallTranslation = find(id);

        stallTranslation.setName(request.getLanguageCode() + "_" + request.getStallId());
        stallTranslation.setTtsScript(request.getTtsScript());

        // Reset audio khi script thay đổi để hệ thống tạo lại file mới (sẽ đè file cũ
        // trên Cloudinary)
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

    @Override
    // @Transactional
    public StallAudioResponse getAudio(Long stallId, String lang, String clientHash) {

        // 1. Tìm bản dịch theo ngôn ngữ khách yêu cầu
        Optional<StallTranslation> requestedOpt = repository.findByStallIdAndLanguageCode(stallId, lang);
        StallTranslation targetTranslation;

        // 2. NẾU CHƯA CÓ -> Tự động tạo Placeholder để kích hoạt dịch thuật & tạo TTS
        if (requestedOpt.isEmpty()) {
            targetTranslation = StallTranslation.builder()
                    .stallId(stallId)
                    .languageCode(lang)
                    .name(lang + "_" + stallId)
                    .ttsScript(null)
                    .audioStatus(AudioStatus.PENDING)
                    .build();
            targetTranslation = repository.save(targetTranslation);
        } else {
            targetTranslation = requestedOpt.get();
        }

        // 3. TRƯỜNG HỢP LÝ TƯỞNG: Ngôn ngữ đích đã sẵn sàng
        if (targetTranslation.getAudioStatus() == AudioStatus.COMPLETED) {
            if (clientHash != null && clientHash.equals(targetTranslation.getAudioHash())) {
                return StallAudioResponse.builder()
                        .needDownload(false)
                        .status(AudioStatus.COMPLETED)
                        .message("Use local audio. Hash matched.")
                        .build();
            }
            return StallAudioResponse.builder()
                    .needDownload(true)
                    .audioUrl(targetTranslation.getAudioUrl())
                    .fileSize(targetTranslation.getFileSize())
                    .audioHash(targetTranslation.getAudioHash())
                    .status(AudioStatus.COMPLETED)
                    .message("Audio generated successfully. Need download.")
                    .build();
        }

        // 4. TRƯỜNG HỢP CẦN GEN MỚI: Đang PENDING hoặc bị ERROR trước đó
        if (targetTranslation.getAudioStatus() == AudioStatus.PENDING
                || targetTranslation.getAudioStatus() == AudioStatus.ERROR) {
            targetTranslation.setAudioStatus(AudioStatus.PROCESSING);
            repository.save(targetTranslation);

            // Kích hoạt tiến trình ngầm (Dịch -> Gen TTS -> Upload)
            audioProcessorService.processAudioAsync(targetTranslation, stallId, lang);
        }

        // 5. UX TỐI ƯU: Không để khách chờ. Trong lúc bản dịch mới đang PROCESSING, trả
        // về bản FALLBACK nghe tạm
        StallTranslation fallbackTranslation = repository.findByStallIdAndLanguageCode(stallId, "en")
                .or(() -> repository.findFirstByStallId(stallId)) // Tiếng Anh không có thì lấy đại tiếng Việt (ngôn ngữ
                                                                  // gốc)
                .orElseThrow(() -> new AppException(ErrorCode.STALL_TRANSLATION_NOT_FOUND));

        if (fallbackTranslation.getAudioStatus() == AudioStatus.COMPLETED) {
            boolean hashMatched = clientHash != null && clientHash.equals(fallbackTranslation.getAudioHash());
            return StallAudioResponse.builder()
                    .needDownload(!hashMatched)
                    .audioUrl(hashMatched ? null : fallbackTranslation.getAudioUrl())
                    .fileSize(hashMatched ? 0L : fallbackTranslation.getFileSize())
                    .audioHash(fallbackTranslation.getAudioHash())
                    .status(AudioStatus.COMPLETED)
                    .message("Requested language is generating. Using fallback language ("
                            + fallbackTranslation.getLanguageCode() + ").")
                    .build();
        }

        return StallAudioResponse.builder()
                .needDownload(false)
                .status(AudioStatus.PROCESSING)
                .message("All audios are currently processing. Please wait.")
                .build();
    }

    private StallTranslation find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private StallTranslationResponse map(StallTranslation stallTranslation) {
        return StallTranslationResponse.builder()
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
 
    @Override
    public void saveOrUpdate(Long stallId, String lang, String script) {
        Optional<StallTranslation> opt = repository.findByStallIdAndLanguageCode(stallId, lang);
 
        StallTranslation translation;
        if (opt.isPresent()) {
            translation = opt.get();
            // Nếu script thay đổi, reset audio
            if (script != null && !script.equals(translation.getTtsScript())) {
                translation.setTtsScript(script);
                translation.setAudioStatus(AudioStatus.PENDING);
                translation.setAudioUrl(null);
                translation.setAudioHash(null);
                translation.setFileSize(0L);
            }
        } else {
            translation = StallTranslation.builder()
                    .stallId(stallId)
                    .languageCode(lang)
                    .name(lang + "_" + stallId)
                    .ttsScript(script)
                    .audioStatus(AudioStatus.PENDING)
                    .build();
        }
        repository.save(translation);
    }
 
    @Override
    @Transactional
    public void deleteAllByStall(Long stallId) {
        repository.deleteByStallId(stallId);
    }
}