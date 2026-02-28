package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.stall.request.StallCreationRequest;
import SmartFoodStreet_Backend.dto.stall.request.StallUpdateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;

import java.util.List;

public interface IStall {

    StallResponse create(StallCreationRequest request);

    StallResponse update(Long id, StallUpdateRequest request);

    void delete(Long id);

    List<StallResponse> getMyStalls();

    List<StallResponse> getByStreet(Long streetId);

    List<StallResponse> getAllAdmin();

    StallResponse getById(Long id);
}