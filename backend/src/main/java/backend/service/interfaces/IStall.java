package backend.service.interfaces;

import backend.dto.stall.request.StallCreateRequest;
import backend.dto.stall.response.StallResponse;

import java.util.List;

public interface IStall {

    StallResponse create(StallCreateRequest request);

    StallResponse getById(Long id);

    List<StallResponse> getByStreet(Long streetId);

    List<StallResponse> getAllActive();

    StallResponse getByVendor(Long vendorId);

    List<StallResponse> getAll();

    StallResponse update(Long id, StallCreateRequest request);

    void delete(Long id);
}