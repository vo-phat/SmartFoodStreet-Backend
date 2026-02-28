package SmartFoodStreet_Backend.dto.visitsesion.request;

import lombok.Data;

@Data
public class StartSessionRequest {

    Long streetId;

    String deviceId;

    Double budgetInitial;

    Double startLatitude;
    Double startLongitude;
}
