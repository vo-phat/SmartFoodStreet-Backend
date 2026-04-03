package SmartFoodStreet_Backend.dto.stall.response;

import SmartFoodStreet_Backend.enums.TriggerType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StallTriggerConfigResponse {
    Long stallId;

    TriggerType triggerType;

    Integer radius;

    Integer triggerDistance;

    Integer cooldownSeconds;

    Integer priority;
}