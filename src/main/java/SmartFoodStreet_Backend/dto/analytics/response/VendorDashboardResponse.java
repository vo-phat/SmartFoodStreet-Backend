package SmartFoodStreet_Backend.dto.analytics.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorDashboardResponse {

    Long stallId;

    Integer totalVisits;
    Integer totalAudioComplete;
    Integer totalVoucherRedeemed;

    Double conversionRate;
}
