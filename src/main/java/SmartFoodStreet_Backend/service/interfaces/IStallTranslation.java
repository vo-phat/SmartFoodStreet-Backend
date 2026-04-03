package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;

public interface IStallTranslation {

    void create(StallTranslationRequest request);

    StallTranslationResponse get(Long stallId, String language);
}