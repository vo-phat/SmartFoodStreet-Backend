package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.street.request.FoodStreetCreationRequest;
import SmartFoodStreet_Backend.dto.street.request.FoodStreetUpdateRequest;
import SmartFoodStreet_Backend.dto.street.response.FoodStreetResponse;

import java.util.List;

public interface IFoodStreet {

    FoodStreetResponse create(FoodStreetCreationRequest request);

    List<FoodStreetResponse> getAll();

    List<FoodStreetResponse> getActive();

    FoodStreetResponse getById(Long id);

    FoodStreetResponse update(Long id, FoodStreetUpdateRequest request);

    void delete(Long id);
}