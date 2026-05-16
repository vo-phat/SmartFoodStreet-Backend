package SmartFoodStreet_Backend.dto.stall.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StallTranslationRequest {
    @NotNull
    Long stallId;

    @NotBlank
    String languageCode;

    String ttsScript;
}