package SmartFoodStreet_Backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class TranslationService {

    private Translate translateClient;

    @PostConstruct
    public void init() {
        try {
            // 1. Sử dụng ClassPathResource để đọc file từ src/main/resources
            org.springframework.core.io.ClassPathResource resource =
                    new org.springframework.core.io.ClassPathResource("tts.json");

            if (!resource.exists()) {
                log.warn("****************************************************************");
                log.warn("WARNING: tts.json not found in src/main/resources.");
                log.warn("Translation Service will be disabled.");
                log.warn("****************************************************************");
                return;
            }

            // 2. Sử dụng try-with-resources để đảm bảo đóng stream sau khi đọc
            try (InputStream credentialsStream = resource.getInputStream()) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

                translateClient = TranslateOptions.newBuilder()
                        .setCredentials(credentials)
                        .build()
                        .getService();

                log.info("Khởi tạo Google Cloud Translation API thành công từ resources.");
            }
        } catch (Exception e) {
            log.error("Lỗi khi khởi tạo Translation API: {}. App will continue without Translation.", e.getMessage());
        }
    }

    /**
     * Dịch văn bản bằng Google Translate
     *
     * @param text       Nội dung cần dịch
     * @param sourceLang Ngôn ngữ gốc (vd: "vi")
     * @param targetLang Ngôn ngữ đích (vd: "ko", "en")
     * @return Văn bản đã được dịch
     */
    public String translateText(String text, String sourceLang, String targetLang) {
        if (translateClient == null) {
            log.warn("Translation Service is not initialized. Returning original text.");
            return text;
        }
        log.info("Đang dịch văn bản từ {} sang {} bằng Google Translate...", sourceLang, targetLang);

        try {
            Translation translation = translateClient.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage(sourceLang),
                    Translate.TranslateOption.targetLanguage(targetLang),
                    Translate.TranslateOption.model("nmt") // Sử dụng Neural Machine Translation để dịch chuẩn ngữ cảnh hơn
            );

            // Google Cloud trả về kết quả dịch thuật
            String translatedText = translation.getTranslatedText();

            // Xóa bỏ các ký tự HTML entities nếu có (VD: &quot; -> ")
            translatedText = translatedText
                    .replace("&quot;", "\"")
                    .replace("&#39;", "'")
                    .replace("&amp;", "&");

            log.info("Dịch thành công: {}", translatedText);
            return translatedText;

        } catch (Exception e) {
            log.error("Lỗi khi dịch văn bản bằng Google API: {}", e.getMessage(), e);
            throw new RuntimeException("TRANSLATION_FAILED");
        }
    }
}