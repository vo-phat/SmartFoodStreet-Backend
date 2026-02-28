package SmartFoodStreet_Backend.dto.stall.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StallResponse {

    Long id;

    Long streetId;
    Long vendorId;

    String name;
    String category;

    Double avgPrice;
    Double minPrice;
    Double maxPrice;

    Double latitude;
    Double longitude;

    Integer radius;
    Integer priority;
    Integer cooldownSeconds;

    Double recommendationScore;

    Boolean isActive;
}