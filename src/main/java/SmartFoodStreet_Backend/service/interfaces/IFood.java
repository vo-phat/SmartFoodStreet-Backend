package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.food.request.FoodRequest;
import SmartFoodStreet_Backend.dto.food.response.FoodResponse;

import java.util.List;

public interface IFood {

    FoodResponse create(FoodRequest request);

    List<FoodResponse> getByStall(Long stallId);
}