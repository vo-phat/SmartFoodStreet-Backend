package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallNearbyResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.entity.StallTriggerConfig;
import SmartFoodStreet_Backend.enums.TriggerType;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.repository.StallTranslationRepository;
import SmartFoodStreet_Backend.repository.StallTriggerConfigRepository;
import SmartFoodStreet_Backend.service.interfaces.IStallTriggerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StallTriggerConfigService implements IStallTriggerConfig {
    private final StallRepository stallRepository;
    private final StallTriggerConfigRepository configRepo;
    private final StallTranslationRepository translationRepo;

    @Override
    public StallTriggerConfigResponse create(StallTriggerConfigRequest stallTriggerConfigRequest) {

        validateTrigger(stallTriggerConfigRequest);

        StallTriggerConfig stallTriggerConfig = StallTriggerConfig.builder()
                .stallId(stallTriggerConfigRequest.getStallId())
                .triggerType(stallTriggerConfigRequest.getTriggerType())
                .radius(stallTriggerConfigRequest.getRadius())
                .triggerDistance(stallTriggerConfigRequest.getTriggerDistance())
                .cooldownSeconds(stallTriggerConfigRequest.getCooldownSeconds())
                .priority(stallTriggerConfigRequest.getPriority())
                .build();

        return mapConfig(configRepo.save(stallTriggerConfig));
    }

    @Override
    public StallTriggerConfigResponse update(Long stallId, StallTriggerConfigRequest stallTriggerConfigRequest) {

        validateTrigger(stallTriggerConfigRequest);

        StallTriggerConfig stallTriggerConfig = configRepo.findById(stallId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        stallTriggerConfig.setTriggerType(stallTriggerConfigRequest.getTriggerType());
        stallTriggerConfig.setRadius(stallTriggerConfigRequest.getRadius());
        stallTriggerConfig.setTriggerDistance(stallTriggerConfigRequest.getTriggerDistance());
        stallTriggerConfig.setCooldownSeconds(stallTriggerConfigRequest.getCooldownSeconds());
        stallTriggerConfig.setPriority(stallTriggerConfigRequest.getPriority());

        return mapConfig(configRepo.save(stallTriggerConfig));
    }

    @Override
    public void delete(Long stallId) {
        configRepo.deleteById(stallId);
    }

    @Override
    public StallTriggerConfigResponse getByStallId(Long stallId) {

        return mapConfig(
                configRepo.findById(stallId)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    // MAIN LOGIC
    @Override
    public List<StallNearbyResponse> getNearby(double lat, double lng, double radius, String languageCode) {

        List<Stall> stalls = stallRepository.findNearbyStalls(lat, lng, radius);

        return stalls.stream().map(stall -> {

                    StallTriggerConfig config = configRepo
                            .findByStallId(stall.getId())
                            .orElse(null);

                    StallTranslation translation = (StallTranslation) translationRepo
                            .findByStallIdAndLanguageCode(stall.getId(), languageCode)
                            .orElse(null);

                    return mapNearby(stall, config, translation);

                })
                .sorted(Comparator.comparing(
                        StallNearbyResponse::getPriority,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    // ===== MAPPER =====

    private StallTriggerConfigResponse mapConfig(StallTriggerConfig stallTriggerConfig) {
        return StallTriggerConfigResponse.builder()
                .stallId(stallTriggerConfig.getStallId())
                .triggerType(stallTriggerConfig.getTriggerType())
                .radius(stallTriggerConfig.getRadius())
                .triggerDistance(stallTriggerConfig.getTriggerDistance())
                .cooldownSeconds(stallTriggerConfig.getCooldownSeconds())
                .priority(stallTriggerConfig.getPriority())
                .build();
    }

    private StallNearbyResponse mapNearby(Stall stall, StallTriggerConfig stallTriggerConfig, StallTranslation stallTranslation) {

        return StallNearbyResponse.builder()
                .stallId(stall.getId())
                .name(stall.getName())
                .category(stall.getCategory())
                .latitude(stall.getLatitude())
                .longitude(stall.getLongitude())

                .triggerType(stallTriggerConfig != null ? stallTriggerConfig.getTriggerType().name() : null)
                .radius(stallTriggerConfig != null ? stallTriggerConfig.getRadius() : null)
                .triggerDistance(stallTriggerConfig != null ? stallTriggerConfig.getTriggerDistance() : null)
                .cooldownSeconds(stallTriggerConfig != null ? stallTriggerConfig.getCooldownSeconds() : null)
                .priority(stallTriggerConfig != null ? stallTriggerConfig.getPriority() : null)

                .audioUrl(stallTranslation != null ? stallTranslation.getAudioUrl() : null)
                .audioStatus(stallTranslation != null && stallTranslation.getAudioStatus() != null
                        ? stallTranslation.getAudioStatus().name() : null)
                .build();
    }

    private void validateTrigger(StallTriggerConfigRequest req) {
        if (req.getTriggerType() == TriggerType.GEOFENCE && req.getRadius() == null)
            throw new AppException(ErrorCode.INVALID_REQUEST);

        if (req.getTriggerType() == TriggerType.DISTANCE && req.getTriggerDistance() == null)
            throw new AppException(ErrorCode.INVALID_REQUEST);
    }
}