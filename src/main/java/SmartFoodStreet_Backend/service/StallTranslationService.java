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

@Service
@RequiredArgsConstructor
public class StallTranslationService implements IStallTranslation {

    private final StallTranslationRepository repository;
    private final AudioProcessorService audioProcessorService; // Inject Service mới tạo ở trên

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

        // Reset audio khi script thay đổi để hệ thống tạo lại file mới
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
    // CORE AUDIO FLOW (Zero Latency & Fallback)
    // =========================

    @Override
    @Transactional
    public StallAudioResponse getAudio(Long stallId, String lang, String clientHash) {

        // Logic Fallback: Ưu tiên ngôn ngữ user chọn -> Tiếng Anh -> Bất kỳ ngôn ngữ nào có sẵn
        StallTranslation stallTranslation = (StallTranslation) repository.findByStallIdAndLanguageCode(stallId, lang)
                .or(() -> repository.findByStallIdAndLanguageCode(stallId, "en"))
                .or(() -> repository.findFirstByStallId(stallId))
                .orElseThrow(() -> new AppException(ErrorCode.STALL_TRANSLATION_NOT_FOUND));

        // Kiểm tra xem đây có phải là bản dịch thay thế (Fallback) không
        boolean isFallback = !stallTranslation.getLanguageCode().equalsIgnoreCase(lang);
        String baseMessage = isFallback ? "Using fallback language (" + stallTranslation.getLanguageCode() + "). " : "";

        // TRẠNG THÁI 1: COMPLETED (Đã có audio sẵn sàng)
        if (stallTranslation.getAudioStatus() == AudioStatus.COMPLETED) {

            // Nếu Hash từ App gửi lên giống với Hash trên Server -> Không cần tải lại
            if (clientHash != null && clientHash.equals(stallTranslation.getAudioHash())) {
                return StallAudioResponse.builder()
                        .needDownload(false)
                        .status(AudioStatus.COMPLETED)
                        .message(baseMessage + "Use local audio. Hash matched.")
                        .build();
            }

            // Nếu Hash khác (hoặc chưa có) -> Yêu cầu tải file mới
            return StallAudioResponse.builder()
                    .needDownload(true)
                    .audioUrl(stallTranslation.getAudioUrl())
                    .fileSize(stallTranslation.getFileSize())
                    .audioHash(stallTranslation.getAudioHash())
                    .status(AudioStatus.COMPLETED)
                    .message(baseMessage + "Generate audio successfully. Need download.")
                    .build();
        }

        // TRẠNG THÁI 2: PROCESSING (Đang trong quá trình tạo)
        if (stallTranslation.getAudioStatus() == AudioStatus.PROCESSING) {
            return StallAudioResponse.builder()
                    .needDownload(false)
                    .status(AudioStatus.PROCESSING)
                    .message(baseMessage + "Audio is currently being generated. Please wait.")
                    .build();
        }

        // TRẠNG THÁI 3: PENDING / ERROR (Chưa có hoặc bị lỗi trước đó)
        if (stallTranslation.getAudioStatus() == AudioStatus.PENDING
                || stallTranslation.getAudioStatus() == AudioStatus.ERROR) {

            // Cập nhật trạng thái ngay lập tức để block các request khác
            stallTranslation.setAudioStatus(AudioStatus.PROCESSING);
            repository.save(stallTranslation);

            // Gọi Proxy Service để chạy Asynchronous thực sự
            audioProcessorService.processAudioAsync(stallTranslation.getId());

            return StallAudioResponse.builder()
                    .needDownload(false)
                    .status(AudioStatus.PROCESSING)
                    .message(baseMessage + "Triggered audio generation in background.")
                    .build();
        }

        throw new AppException(ErrorCode.INVALID_REQUEST);
    }

    // =========================
    // INTERNAL METHODS
    // =========================

    private StallTranslation find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private StallTranslationResponse map(StallTranslation stallTranslation) {
        return StallTranslationResponse.builder()
                .stallId(stallTranslation.getId())           // Đã sửa lỗi map id
                .stallId(stallTranslation.getStallId()) // Đã sửa lỗi map stallId
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