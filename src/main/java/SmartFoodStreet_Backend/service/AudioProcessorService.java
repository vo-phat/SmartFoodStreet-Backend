package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.response.CloudinaryResponse;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.enums.AudioStatus;
import SmartFoodStreet_Backend.repository.StallTranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioProcessorService {

    private final StallTranslationRepository repository;
    private final TtsService ttsService;
    private final CloudinaryService cloudinaryService;

    @Async
    public void processAudioAsync(Long translationId) {
        StallTranslation stallTranslation = repository.findById(translationId).orElseThrow();

        try {
            log.info("Bắt đầu tạo Audio cho translation ID: {}", translationId);

            // 1. Tối ưu giọng đọc theo ngôn ngữ
            String voiceConfig = getVoiceConfigForLanguage(stallTranslation.getLanguageCode());

            // 2. Gọi API TTS
            // Lưu ý: Hãy điều chỉnh lại ttsService.generate của bạn nếu cần nhận thêm tham số giọng đọc
            byte[] audio = ttsService.generate(stallTranslation.getTtsScript(), stallTranslation.getLanguageCode());

            // 3. Upload lên Cloudinary
            CloudinaryResponse upload = cloudinaryService.uploadAudio(audio, stallTranslation.getName());

            // 4. Tính toán Hash (SHA-256 hoặc MD5)
            String hash = DigestUtils.md5DigestAsHex(audio);

            // 5. Cập nhật thành công
            stallTranslation.setAudioUrl(upload.getUrl());
            stallTranslation.setFileSize(upload.getBytes());
            stallTranslation.setAudioHash(hash);
            stallTranslation.setAudioStatus(AudioStatus.COMPLETED);

            log.info("Tạo Audio thành công cho translation ID: {}", translationId);

        } catch (Exception e) {
            log.error("Lỗi khi tạo Audio cho translation ID: {}", translationId, e);
            stallTranslation.setAudioStatus(AudioStatus.ERROR);
        }

        // Lưu lại trạng thái cuối cùng vào Database
        repository.save(stallTranslation);
    }

    // Cấu hình linh hoạt giọng đọc theo ngôn ngữ
    private String getVoiceConfigForLanguage(String languageCode) {
        return switch (languageCode.toLowerCase()) {
            case "vi" -> "vi-VN-Standard-A"; // Tiếng Việt (Nữ)
            case "en" -> "en-US-Neural2-F";  // Tiếng Anh (Nữ, giọng tự nhiên)
            case "ko" -> "ko-KR-Standard-A"; // Tiếng Hàn
            case "ja" -> "ja-JP-Standard-A"; // Tiếng Nhật
            default -> "en-US-Standard-A";   // Mặc định
        };
    }
}