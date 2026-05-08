package SmartFoodStreet_Backend.dto.visitsesion.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AnalyticsDashboardResponse {

    private long totalQrScans;

    private long uniqueHomeVisitors;

    private Map<Long, Long> audioPerStall;
}