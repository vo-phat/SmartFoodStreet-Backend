package SmartFoodStreet_Backend.dto.visitsesion.response;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class VisitSessionResponse {

    Long id;

    Long streetId;

    String deviceId;

    Double budgetInitial;
    Double budgetRemaining;

    Double startLatitude;
    Double startLongitude;

    Timestamp startedAt;
    Timestamp endedAt;
}