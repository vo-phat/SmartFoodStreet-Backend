package SmartFoodStreet_Backend.dto.stall.request;

import lombok.Data;

@Data
public class StallCreationRequest {

    Long streetId;

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
}
