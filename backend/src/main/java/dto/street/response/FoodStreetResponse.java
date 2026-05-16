package SmartFoodStreet_Backend.dto.street.response;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class FoodStreetResponse {

    Long id;
    String name;
    String description;
    String address;
    String city;
    Double latitude;
    Double longitude;
    Boolean isActive;
    Timestamp createdAt;
    Timestamp updatedAt;
}