package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallNearbyResponse;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;
import SmartFoodStreet_Backend.service.interfaces.IStallTriggerConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stall-trigger-config")
@RequiredArgsConstructor
public class StallTriggerConfigController {

    private final IStallTriggerConfig service;

    @PostMapping
    public ApiResponse<StallTriggerConfigResponse> create(@Valid @RequestBody StallTriggerConfigRequest request) {

        return ApiResponse.<StallTriggerConfigResponse>builder()
                .result(service.create(request))
                .build();
    }

    @PutMapping("/{stallId}")
    public ApiResponse<StallTriggerConfigResponse> update(@PathVariable Long stallId, @Valid @RequestBody StallTriggerConfigRequest request) {

        return ApiResponse.<StallTriggerConfigResponse>builder()
                .result(service.update(stallId, request))
                .build();
    }

    @DeleteMapping("/{stallId}")
    public ApiResponse<Void> delete(@PathVariable Long stallId) {

        service.delete(stallId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/{stallId}")
    public ApiResponse<StallTriggerConfigResponse> getByStallId(
            @PathVariable Long stallId) {

        return ApiResponse.<StallTriggerConfigResponse>builder()
                .result(service.getByStallId(stallId))
                .build();
    }

    // MAIN API
    @GetMapping("/nearby")
    public ApiResponse<List<StallNearbyResponse>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "500") double radius,
            @RequestParam(defaultValue = "vi") String languageCode
    ) {

        return ApiResponse.<List<StallNearbyResponse>>builder()
                .result(service.getNearby(lat, lng, radius, languageCode))
                .build();
    }
}