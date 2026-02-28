package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.gps.request.GpsCheckRequest;
import SmartFoodStreet_Backend.dto.gps.response.GpsCheckResponse;

public interface IGps {

    GpsCheckResponse check(GpsCheckRequest request);
}