package backend.service.interfaces;

import backend.dto.food.request.FoodRequest;
import backend.dto.food.response.FoodResponse;

import java.util.List;

public interface IFood {

    FoodResponse create(FoodRequest request);

    List<FoodResponse> getByStall(Long stallId);

    List<FoodResponse> getAll();

    FoodResponse update(Long id, FoodRequest request);

    void delete(Long id);
}