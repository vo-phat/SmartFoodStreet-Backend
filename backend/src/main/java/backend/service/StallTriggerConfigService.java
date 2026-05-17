package backend.service;

import backend.common.exception.AppException;
import backend.common.exception.ErrorCode;
import backend.dto.stall.request.StallTriggerConfigRequest;
import backend.dto.stall.response.StallTriggerConfigResponse;
import backend.entity.StallTriggerConfig;
import backend.repository.StallTriggerConfigRepository;
import backend.service.interfaces.IStallTriggerConfig;
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