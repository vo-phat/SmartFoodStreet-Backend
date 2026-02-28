package SmartFoodStreet_Backend.dto.analytics.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminOverviewResponse {

    Long totalSessions;
    Long totalEnterEvents;
    Long totalAudioComplete;
    Long totalVoucherGenerated;
    Long totalVoucherRedeemed;

    Double redemptionRate;
    Double audioCompletionRate;

    Long estimatedRevenueImpact;
}
