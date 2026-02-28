package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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
    Long id;

    @Column(name = "session_id")
    Long sessionId;

    @Column(name = "stall_id")
    Long stallId;

    @Enumerated(EnumType.STRING)
    EventType eventType;

    @Column(name = "event_time")
    Timestamp eventTime;

    public enum EventType {
        ENTER_GEOFENCE,
        EXIT_GEOFENCE,
        AUDIO_START,
        AUDIO_COMPLETE,
        QR_SCAN,
        VIEW_DETAIL,
        VOUCHER_GENERATED,
        VOUCHER_REDEEMED
    }
}