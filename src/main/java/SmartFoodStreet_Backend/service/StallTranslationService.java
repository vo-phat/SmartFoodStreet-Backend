package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;
import SmartFoodStreet_Backend.entity.StallTranslation;
import SmartFoodStreet_Backend.repository.StallTranslationRepository;
import SmartFoodStreet_Backend.service.interfaces.IStallTranslation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StallTranslationService implements IStallTranslation {

    private final StallTranslationRepository repository;

    @Override
    public void create(StallTranslationRequest stallTranslationRequest) {

        boolean exists = repository.existsByStallIdAndLanguageCode(
                stallTranslationRequest.getStallId(), stallTranslationRequest.getLanguageCode());

        if (exists)
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS);

        StallTranslation stallTranslation = new StallTranslation();
        stallTranslation.setStallId(stallTranslationRequest.getStallId());
        stallTranslation.setLanguageCode(stallTranslationRequest.getLanguageCode());
        stallTranslation.setName(stallTranslation.getLanguageCode() + "_" + stallTranslation.getStallId());
        stallTranslation.setTtsScript(stallTranslationRequest.getTtsScript());

        repository.save(stallTranslation);
    }

    @Override
    public StallTranslationResponse get(Long stallId, String languageCode) {

        StallTranslation stallTranslation = (StallTranslation) repository
                .findByStallIdAndLanguageCode(stallId, languageCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        return StallTranslationResponse.builder()
                .stallId(stallTranslation.getStallId())
                .language(stallTranslation.getLanguageCode())
                .name(stallTranslation.getName())
                .ttsScript(stallTranslation.getTtsScript())
                .build();
    }
}