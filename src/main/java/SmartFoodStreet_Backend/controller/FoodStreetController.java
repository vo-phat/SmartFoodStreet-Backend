package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.street.request.FoodStreetCreationRequest;
import SmartFoodStreet_Backend.dto.street.request.FoodStreetUpdateRequest;
import SmartFoodStreet_Backend.dto.street.response.FoodStreetResponse;
import SmartFoodStreet_Backend.service.interfaces.IFoodStreet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/streets")
@RequiredArgsConstructor
public class FoodStreetController {

    private final IFoodStreet service;

    // ================= ADMIN =================

    @GetMapping("/admin")
    public ApiResponse<List<FoodStreetResponse>> getAll() {
        return ApiResponse.<List<FoodStreetResponse>>builder()
                .result(service.getAll())
                .build();
    }

    @PostMapping
    public ApiResponse<FoodStreetResponse> create(
            @RequestBody @Valid FoodStreetCreationRequest request) {
        return ApiResponse.<FoodStreetResponse>builder()
                .result(service.create(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<FoodStreetResponse> update(
            @PathVariable Long id,
            @RequestBody FoodStreetUpdateRequest request) {
        return ApiResponse.<FoodStreetResponse>builder()
                .result(service.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.<Void>builder()
                .message("Deleted successfully")
                .build();
    }

    // ================= PUBLIC =================

    @GetMapping
    public ApiResponse<List<FoodStreetResponse>> getActive() {
        return ApiResponse.<List<FoodStreetResponse>>builder()
                .result(service.getActive())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<FoodStreetResponse> getById(@PathVariable Long id) {
        return ApiResponse.<FoodStreetResponse>builder()
                .result(service.getById(id))
                .build();
    }
}