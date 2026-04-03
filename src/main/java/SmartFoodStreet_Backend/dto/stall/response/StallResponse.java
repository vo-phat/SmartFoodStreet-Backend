package SmartFoodStreet_Backend.dto.stall.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StallResponse {
    Long id;

    String name;

    String category;

    Double latitude;

    Double longitude;

    String image;

    Boolean isActive;
}