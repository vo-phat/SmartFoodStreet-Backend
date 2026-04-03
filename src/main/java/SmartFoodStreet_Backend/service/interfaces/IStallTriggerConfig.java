package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallNearbyResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;

import java.util.List;

public interface IStallTriggerConfig {

    StallTriggerConfigResponse create(StallTriggerConfigRequest request);

    StallTriggerConfigResponse update(Long stallId, StallTriggerConfigRequest request);

    void delete(Long stallId);

    StallTriggerConfigResponse getByStallId(Long stallId);

    List<StallNearbyResponse> getNearby(double lat, double lng, double radius, String languageCode);
}