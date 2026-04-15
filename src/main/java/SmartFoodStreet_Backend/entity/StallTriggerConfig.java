package SmartFoodStreet_Backend.entity;

import SmartFoodStreet_Backend.enums.TriggerType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stall_trigger_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StallTriggerConfig {
    @Id
    Long stallId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "stall_id")
    Stall stall;

    @Enumerated(EnumType.STRING)
    TriggerType triggerType;

    Integer radius;

    Integer triggerDistance;

    Integer cooldownSeconds;

    Integer priority;
}