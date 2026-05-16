package SmartFoodStreet_Backend.enums;

import lombok.Getter;

@Getter
public enum LanguageCode {

    VI_VN("vi-VN", "Tiếng Việt", "vi-VN-Standard-A"),
    EN_US("en-US", "English", "en-US-Neural2-F"),
    ZH_CN("zh-CN", "中文", "cmn-CN-Standard-A"),
    ES_ES("es-ES", "Español", "es-ES-Standard-A"),
    HI_IN("hi-IN", "हिन्दी", "hi-IN-Standard-A"),
    AR_XA("ar-XA", "العربية", "ar-XA-Standard-A"),
    FR_FR("fr-FR", "Français", "fr-FR-Standard-A"),
    RU_RU("ru-RU", "Русский", "ru-RU-Standard-A"),
    PT_PT("pt-PT", "Português", "pt-PT-Standard-A"),
    JA_JP("ja-JP", "日本語", "ja-JP-Standard-A"),
    KO_KR("ko-KR", "한국어", "ko-KR-Standard-A");

    private final String code;
    private final String label;
    private final String voice;

    LanguageCode(String code, String label, String voice) {
        this.code = code;
        this.label = label;
        this.voice = voice;
    }

    public static LanguageCode fromCode(String code) {
        for (LanguageCode lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }

        throw new IllegalArgumentException("Unsupported language: " + code);
    }
}