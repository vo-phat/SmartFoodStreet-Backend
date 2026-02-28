package SmartFoodStreet_Backend.dto.analytics.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreetDashboardResponse {

    Long streetId;
    Long totalSessions;
    Long totalEnterEvents;
    Long totalVoucherRedeemed;
}
