package SmartFoodStreet_Backend.dto.stall.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StallCreateRequest {
    @NotNull
    Long streetId;

    @NotNull
    Long vendorId;

    @NotBlank
    @Size(max = 255)
    String name;

    String category;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    Double longitude;

    String image;
}