package SmartFoodStreet_Backend.dto.stall.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StallTriggerConfigResponse {
    Long stallId;

    String triggerType;

    Integer radius;

    Integer triggerDistance;

    Integer cooldownSeconds;

    Integer priority;
}