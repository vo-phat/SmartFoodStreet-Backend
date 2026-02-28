package SmartFoodStreet_Backend.dto.voucher.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VoucherResponse {

    private String voucherCode;
    private Integer discountPercent;
    private Double estimatedValue;
    private Boolean isRedeemed;
    private LocalDateTime createdAt;
}