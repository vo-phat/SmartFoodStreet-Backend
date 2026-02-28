package SmartFoodStreet_Backend.dto.analytics.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeatmapPointResponse {

    Double latitude;
    Double longitude;
    Long count;
}