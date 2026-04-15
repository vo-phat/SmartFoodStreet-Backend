package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.stall.request.StallTranslationRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallAudioResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTranslationResponse;

import java.util.List;

public interface IStallTranslation {

    StallTranslationResponse create(StallTranslationRequest request);

    StallTranslationResponse update(Long id, StallTranslationRequest request);

    StallTranslationResponse getById(Long id);

    List<StallTranslationResponse> getByStall(Long stallId);

    void delete(Long id);

    StallAudioResponse getAudio(Long stallId, String lang, String clientHash);
 
    void saveOrUpdate(Long stallId, String lang, String script);
 
    void deleteAllByStall(Long stallId);
 }
