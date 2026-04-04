package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.food.request.FoodRequest;
import SmartFoodStreet_Backend.dto.food.response.FoodResponse;
import SmartFoodStreet_Backend.entity.Food;
import SmartFoodStreet_Backend.repository.FoodRepository;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.service.interfaces.IFood;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService implements IFood {
    private final FoodRepository repository;
    private final StallRepository stallRepository;

    @Override
    @PreAuthorize("hasAuthority('FOOD_CREATE')")
    public FoodResponse create(FoodRequest foodRequest) {

        if (!stallRepository.existsById(foodRequest.getStallId()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);

        Food food = new Food();
        food.setStallId(foodRequest.getStallId());
        food.setName(foodRequest.getName());
        food.setPrice(foodRequest.getPrice());
        food.setDescription(foodRequest.getDescription());
        food.setImage(foodRequest.getImage());
        food.setIsAvailable(true);

        repository.save(food);

        return map(food);
    }

    @Override
    public List<FoodResponse> getByStall(Long stallId) {
        return repository.findByStallId(stallId)
                .stream().map(this::map).toList();
    }

    @Override
    public FoodResponse getById(Long id) {
        Food food = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        return map(food);
    }

    @Override
    @PreAuthorize("hasAuthority('FOOD_UPDATE')")
    public FoodResponse update(Long id, FoodRequest foodRequest) {
        Food food = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!food.getStallId().equals(foodRequest.getStallId())) {
            if (!stallRepository.existsById(foodRequest.getStallId())) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            food.setStallId(foodRequest.getStallId());
        }

        food.setName(foodRequest.getName());
        food.setPrice(foodRequest.getPrice());
        food.setDescription(foodRequest.getDescription());
        food.setImage(foodRequest.getImage());

        repository.save(food);

        return map(food);
    }

    @Override
    @PreAuthorize("hasAuthority('FOOD_DELETE')")
    public void delete(Long id) {
        Food food = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        repository.delete(food);
    }

    private FoodResponse map(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .price(food.getPrice())
                .description(food.getDescription())
                .image(food.getImage())
                .isAvailable(food.getIsAvailable())
                .build();
    }
}