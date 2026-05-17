package backend.service.interfaces;

import backend.dto.stall.request.StallTriggerConfigRequest;
import backend.dto.stall.response.StallTriggerConfigResponse;

public interface IStallTriggerConfig {

    void createOrUpdate(StallTriggerConfigRequest request);

    StallTriggerConfigResponse getByStall(Long stallId);
}