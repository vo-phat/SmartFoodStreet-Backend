package backend.service.interfaces;

import backend.dto.street.request.FoodStreetCreationRequest;
import backend.dto.street.request.FoodStreetUpdateRequest;
import backend.dto.street.response.FoodStreetResponse;

import java.util.List;

public interface IFoodStreet {

    FoodStreetResponse create(FoodStreetCreationRequest request);

    List<FoodStreetResponse> getAll();

    List<FoodStreetResponse> getActive();

    FoodStreetResponse getById(Long id);

    FoodStreetResponse update(Long id, FoodStreetUpdateRequest request);

    void delete(Long id);
}