package SmartFoodStreet_Backend.dto.voucher.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class GenerateVoucherRequest {

    @NotNull(message = "SESSION_ID_REQUIRED")
    @Positive
    private Long sessionId;

    @NotNull(message = "STALL_ID_REQUIRED")
    @Positive
    private Long stallId;
}