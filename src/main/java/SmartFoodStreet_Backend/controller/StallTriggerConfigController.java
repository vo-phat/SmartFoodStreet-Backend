package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;
import SmartFoodStreet_Backend.service.interfaces.IStallTriggerConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stall-trigger-config")
@RequiredArgsConstructor
public class StallTriggerConfigController {

    private final IStallTriggerConfig configService;

    @PostMapping
    public ApiResponse<Void> createOrUpdate(@Valid @RequestBody StallTriggerConfigRequest req) {
        configService.createOrUpdate(req);
        return ApiResponse.<Void>builder()
                .message("Successfully")
                .build();
    }

    @GetMapping("/{stallId}")
    public ApiResponse<StallTriggerConfigResponse> getByStall(@PathVariable Long stallId) {
        return ApiResponse.<StallTriggerConfigResponse>builder()
                .result(configService.getByStall(stallId))
                .build();
    }
}