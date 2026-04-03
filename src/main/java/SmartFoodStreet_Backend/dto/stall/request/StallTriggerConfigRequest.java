package SmartFoodStreet_Backend.dto.stall.request;

import SmartFoodStreet_Backend.enums.TriggerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StallTriggerConfigRequest {
    @NotNull
    private Long stallId;

    @NotNull
    private TriggerType triggerType;

    private Integer radius;
    private Integer triggerDistance;

    @NotNull
    @Min(1)
    private Integer cooldownSeconds;

    @NotNull
    private Integer priority;
}