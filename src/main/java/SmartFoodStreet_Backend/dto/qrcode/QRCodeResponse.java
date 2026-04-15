package SmartFoodStreet_Backend.dto.qrcode;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResponse {
    private Long id;
    private String name;
    private String code;
    private Long stallId;
    private String stallName;
    private Boolean isActive;
    private Integer scanCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
