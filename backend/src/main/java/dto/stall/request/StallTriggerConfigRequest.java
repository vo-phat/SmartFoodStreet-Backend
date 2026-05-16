package SmartFoodStreet_Backend.dto.stall.request;

import SmartFoodStreet_Backend.enums.TriggerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StallTriggerConfigRequest {
    @NotNull
    Long stallId;

    @NotNull
    TriggerType triggerType;

    @Min(1)
    Integer radius;

    @Min(1)
    Integer triggerDistance;

    @Min(1)
    Integer cooldownSeconds;

    @Min(1)
    Integer priority;
}