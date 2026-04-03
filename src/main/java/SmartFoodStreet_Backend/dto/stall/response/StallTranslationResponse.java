package SmartFoodStreet_Backend.dto.stall.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StallTranslationResponse {
    Long stallId;

    String language;

    String name;

    String ttsScript;
}