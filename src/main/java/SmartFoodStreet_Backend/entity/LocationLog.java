package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    VisitSession session;

    @Column(precision = 10, scale = 8)
    BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    BigDecimal longitude;

    @Column(name = "recorded_at")
    LocalDateTime recordedAt;
}