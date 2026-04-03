package SmartFoodStreet_Backend.dto.stall.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StallNearbyResponse {
    Long stallId;
    String name;
    String category;

    Double latitude;
    Double longitude;

    String triggerType;
    Integer radius;
    Integer triggerDistance;
    Integer cooldownSeconds;
    Integer priority;

    String audioUrl;
    String audioStatus;
}