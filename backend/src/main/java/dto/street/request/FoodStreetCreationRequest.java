package SmartFoodStreet_Backend.dto.street.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FoodStreetCreationRequest {
    @NotBlank
    String name;

    String description;
    String address;
    String city;

    Double latitude;
    Double longitude;
}