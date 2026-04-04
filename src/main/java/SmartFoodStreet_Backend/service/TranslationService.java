package SmartFoodStreet_Backend.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TranslationService {

    private Translate translateClient;

    @PostConstruct
    public void init() {
        translateClient = TranslateOptions.getDefaultInstance().getService();
        log.info("Khởi tạo Google Cloud Translation API thành công.");
    }

    /**
     * Dịch văn bản bằng Google Translate
     * @param text Nội dung cần dịch
     * @param sourceLang Ngôn ngữ gốc (vd: "vi")
     * @param targetLang Ngôn ngữ đích (vd: "ko", "en")
     * @return Văn bản đã được dịch
     */
    public String translateText(String text, String sourceLang, String targetLang) {
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