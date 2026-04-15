package SmartFoodStreet_Backend.dto.stall.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StallResponse {
    Long id;
    Long streetId;
    Long vendorId;

    String name;

    String category;

    String description;

    String latitude;
    String longitude;

    String image;

    String script;

    Boolean isActive;
}