package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stall_id", nullable = false)
    private Long stallId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "voucher_code", unique = true)
    private String voucherCode;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "estimated_value")
    private Double estimatedValue;

    @Column(name = "is_redeemed")
    private Boolean isRedeemed;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}