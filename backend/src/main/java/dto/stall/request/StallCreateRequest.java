package SmartFoodStreet_Backend.dto.stall.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StallCreateRequest {
    Long streetId;

    Long vendorId;

    @Size(max = 255)
    String name;

    String category;
    String description;

    String latitude;

    String longitude;

    String image;

    String script;

    Boolean isActive;
}