package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "analytics_daily",
        uniqueConstraints = @UniqueConstraint(columnNames = {"date", "stall_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(name = "stall_id")
    private Long stallId;

    private Integer totalVisits;

    private Integer totalAudioPlays;
}