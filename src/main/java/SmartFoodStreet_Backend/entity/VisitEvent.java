package SmartFoodStreet_Backend.entity;

import SmartFoodStreet_Backend.enums.VisitEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "stall_id")
    private Long stallId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private VisitEventType eventType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}