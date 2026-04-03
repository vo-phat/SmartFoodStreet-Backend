package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;
import SmartFoodStreet_Backend.entity.StallTriggerConfig;
import SmartFoodStreet_Backend.repository.StallTriggerConfigRepository;
import SmartFoodStreet_Backend.service.interfaces.IStallTriggerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StallTriggerConfigService implements IStallTriggerConfig {

    private final StallTriggerConfigRepository repository;

    @Override
    public void createOrUpdate(StallTriggerConfigRequest req) {

        StallTriggerConfig config = repository.findByStallId(req.getStallId())
                .orElse(new StallTriggerConfig());

        config.setStallId(req.getStallId());
        config.setTriggerType(req.getTriggerType());
        config.setRadius(req.getRadius());
        config.setTriggerDistance(req.getTriggerDistance());
        config.setCooldownSeconds(req.getCooldownSeconds());
        config.setPriority(req.getPriority());

        repository.save(config);
    }

    @Override
    public StallTriggerConfigResponse getByStall(Long stallId) {
        StallTriggerConfig c = repository.findByStallId(stallId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        return StallTriggerConfigResponse.builder()
                .stallId(c.getStallId())
                .triggerType(c.getTriggerType().name())
                .radius(c.getRadius())
                .triggerDistance(c.getTriggerDistance())
                .cooldownSeconds(c.getCooldownSeconds())
                .priority(c.getPriority())
                .build();
    }
}