package SmartFoodStreet_Backend.dto.translation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranslationRequest {
    @NotBlank(message = "Văn bản không được để trống")
    String text;

    @NotBlank(message = "Ngôn ngữ gốc không được để trống (vd: vi)")
    String sourceLang;

    @NotBlank(message = "Ngôn ngữ đích không được để trống (vd: ko)")
    String targetLang;
}