package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.stall.request.StallCreateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;

import java.util.List;

public interface IStall {

    StallResponse create(StallCreateRequest request);

    StallResponse getById(Long id);

    List<StallResponse> getByStreet(Long streetId);

    StallResponse update(Long id, StallCreateRequest request);

    void delete(Long id);
}