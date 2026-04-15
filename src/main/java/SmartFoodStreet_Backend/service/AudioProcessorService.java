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
    private final TranslationService translationService;

    // QUAN TRỌNG: Nhận thẳng Object StallTranslation thay vì Long translationId
    @Async
    public void processAudioAsync(StallTranslation stallTranslation, Long stallId, String targetLanguage) {

        try {
            log.info("Bắt đầu xử lý Audio ngầm cho stall: {}, language: {}", stallId, targetLanguage);

            String scriptToRead = stallTranslation.getTtsScript();

            // 1. DỊCH (Nếu kịch bản đang trống)
            if (scriptToRead == null || scriptToRead.trim().isEmpty()) {
                StallTranslation baseTranslation = (StallTranslation) repository.findByStallIdAndLanguageCode(stallId, "vi")
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy bản gốc tiếng Việt để dịch"));

                scriptToRead = translationService.translateText(baseTranslation.getTtsScript(), "vi", targetLanguage);
                stallTranslation.setTtsScript(scriptToRead); // Lưu lại bản dịch
            }

            // 2. LẤY GIỌNG ĐỌC
            String voiceConfig = getVoiceConfigForLanguage(targetLanguage);

            // 3. GEN AUDIO
            byte[] audio = ttsService.generate(scriptToRead, targetLanguage, voiceConfig);

            // 4. UPLOAD LÊN CLOUD (Ghi đè)
            String publicId = "stall_" + stallId + "_" + targetLanguage;
            CloudinaryResponse upload = cloudinaryService.uploadAudioWithOverwrite(audio, publicId);

            // 5. CẬP NHẬT THÀNH CÔNG
            String hash = DigestUtils.md5DigestAsHex(audio);
            stallTranslation.setAudioUrl(upload.getUrl());
            stallTranslation.setFileSize(upload.getBytes());
            stallTranslation.setAudioHash(hash);
            stallTranslation.setAudioStatus(AudioStatus.COMPLETED);

            log.info("Xử lý thành công ngôn ngữ: {}", targetLanguage);

        } catch (Exception e) {
            log.error("Lỗi khi xử lý ngầm Audio cho ngôn ngữ {}: {}", targetLanguage, e.getMessage(), e);
            stallTranslation.setAudioStatus(AudioStatus.ERROR);
        }

        // Lưu trạng thái cuối cùng vào Database (Dù thành công hay lỗi)
        repository.save(stallTranslation);
    }

    private String getVoiceConfigForLanguage(String languageCode) {
        return switch (languageCode.toLowerCase()) {
            // Châu Á
            case "vi", "vi-vn" -> "vi-VN-Standard-A";          // Tiếng Việt
            case "ko", "ko-kr" -> "ko-KR-Standard-A";          // Tiếng Hàn
            case "ja", "ja-jp" -> "ja-JP-Standard-A";          // Tiếng Nhật
            case "zh", "zh-cn" -> "cmn-CN-Standard-A";         // Tiếng Trung (Giản thể/Đại lục)
            case "zh-tw" -> "cmn-TW-Standard-A";               // Tiếng Trung (Phồn thể/Đài Loan)
            case "th", "th-th" -> "th-TH-Standard-A";          // Tiếng Thái
            case "id", "id-id" -> "id-ID-Standard-A";          // Tiếng Indonesia
            case "hi", "hi-in" -> "hi-IN-Standard-A";          // Tiếng Hindi (Ấn Độ)

            // Tiếng Anh
            case "en", "en-us" -> "en-US-Neural2-F";           // Tiếng Anh (Mỹ)
            case "en-gb" -> "en-GB-Standard-A";                // Tiếng Anh (Anh)
            case "en-au" -> "en-AU-Standard-A";                // Tiếng Anh (Úc)
            case "en-in" -> "en-IN-Standard-A";                // Tiếng Anh (Ấn Độ)

            // Châu Âu
            case "fr", "fr-fr" -> "fr-FR-Standard-A";          // Tiếng Pháp
            case "de", "de-de" -> "de-DE-Standard-A";          // Tiếng Đức
            case "es", "es-es" -> "es-ES-Standard-A";          // Tiếng Tây Ban Nha (Tây Ban Nha)
            case "it", "it-it" -> "it-IT-Standard-A";          // Tiếng Ý
            case "ru", "ru-ru" -> "ru-RU-Standard-A";          // Tiếng Nga
            case "nl", "nl-nl" -> "nl-NL-Standard-A";          // Tiếng Hà Lan
            case "pl", "pl-pl" -> "pl-PL-Standard-A";          // Tiếng Ba Lan

            // Châu Mỹ & Khác
            case "es-mx" -> "es-MX-Standard-A";                // Tiếng Tây Ban Nha (Mexico/Mỹ Latinh)
            case "pt", "pt-br" -> "pt-BR-Standard-A";          // Tiếng Bồ Đào Nha (Brazil)
            case "pt-pt" -> "pt-PT-Standard-A";                // Tiếng Bồ Đào Nha (Bồ Đào Nha)
            case "ar" -> "ar-XA-Standard-A";                   // Tiếng Ả Rập (Tiêu chuẩn)

            // Mặc định
            default -> "en-US-Standard-A";
        };
    }
}