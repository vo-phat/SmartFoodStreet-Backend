package SmartFoodStreet_Backend.dto.gps.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GpsCheckRequest {

    @NotNull(message = "SESSION_ID_REQUIRED")
    @Positive(message = "SESSION_ID_INVALID")
    private Long sessionId;

    @NotNull(message = "LATITUDE_REQUIRED")
    @DecimalMin(value = "-90.0", message = "LATITUDE_INVALID")
    @DecimalMax(value = "90.0", message = "LATITUDE_INVALID")
    private Double latitude;

    @NotNull(message = "LONGITUDE_REQUIRED")
    @DecimalMin(value = "-180.0", message = "LONGITUDE_INVALID")
    @DecimalMax(value = "180.0", message = "LONGITUDE_INVALID")
    private Double longitude;
}