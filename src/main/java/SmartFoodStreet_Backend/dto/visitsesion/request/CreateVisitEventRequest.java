package SmartFoodStreet_Backend.dto.visitsesion.request;

import SmartFoodStreet_Backend.enums.VisitEventType;
import lombok.Data;

@Data
public class CreateVisitEventRequest {

    private String deviceId;

    private Long stallId;

    private VisitEventType eventType;
}