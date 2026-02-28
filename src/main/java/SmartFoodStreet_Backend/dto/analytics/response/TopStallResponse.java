package SmartFoodStreet_Backend.dto.analytics.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopStallResponse {

    Long stallId;
    String stallName;
    Integer totalVisits;
    Integer totalVoucherRedeemed;
}