package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;

public interface IStallTriggerConfig {

    void createOrUpdate(StallTriggerConfigRequest request);

    StallTriggerConfigResponse getByStall(Long stallId);
}