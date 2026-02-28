package SmartFoodStreet_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "visit_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "street_id")
    FoodStreet street;

    String deviceId;

    Double budgetInitial;
    Double budgetRemaining;

    Double startLatitude;
    Double startLongitude;

    @Column(name = "started_at")
    Timestamp startedAt;

    @Column(name = "ended_at")
    Timestamp endedAt;
}