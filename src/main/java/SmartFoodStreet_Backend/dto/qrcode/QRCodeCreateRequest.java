package SmartFoodStreet_Backend.dto.qrcode;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeCreateRequest {
    private String name;
    private String code;
    private Long stallId;
    private Boolean isActive;
}
