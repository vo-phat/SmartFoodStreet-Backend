package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.dto.food.request.FoodRequest;
import SmartFoodStreet_Backend.dto.food.response.FoodResponse;
import SmartFoodStreet_Backend.service.interfaces.IFood;
import SmartFoodStreet_Backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

    private final IFood foodService;

    @PostMapping
    public ApiResponse<FoodResponse> create(@Valid @RequestBody FoodRequest foodRequest) {
        return ApiResponse.<FoodResponse>builder()
                .result(foodService.create(foodRequest))
                .build();
    }

    @GetMapping("/stall/{stallId}")
    public ApiResponse<List<FoodResponse>> getByStall(@PathVariable Long stallId) {
        return ApiResponse.<List<FoodResponse>>builder()
                .result(foodService.getByStall(stallId))
                .build();
    }
}