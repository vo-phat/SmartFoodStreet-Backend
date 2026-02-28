package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "stall_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StallStatistics {

    @Id
    @Column(name = "stall_id")
    Long stallId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "stall_id")
    Stall stall;

    @Column(name = "total_visits")
    Integer totalVisits;

    @Column(name = "total_audio_complete")
    Integer totalAudioComplete;

    @Column(name = "total_voucher_redeemed")
    Integer totalVoucherRedeemed;

    @Column(name = "last_updated")
    LocalDateTime lastUpdated;
}