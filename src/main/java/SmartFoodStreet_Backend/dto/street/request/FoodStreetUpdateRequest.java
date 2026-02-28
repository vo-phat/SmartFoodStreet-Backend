package SmartFoodStreet_Backend.dto.street.request;

import lombok.Data;

@Data
public class FoodStreetUpdateRequest {
    String name;
    String description;
    String address;
    String city;

    Double latitude;
    Double longitude;

    Boolean isActive;
}