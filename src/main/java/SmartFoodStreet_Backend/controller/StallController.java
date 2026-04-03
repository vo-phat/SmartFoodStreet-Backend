package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallCreateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;
import SmartFoodStreet_Backend.service.interfaces.IStall;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stalls")
@RequiredArgsConstructor
public class StallController {

    private final IStall stallService;

    @PostMapping
    public ApiResponse<StallResponse> create(@Valid @RequestBody StallCreateRequest stallCreateRequest) {
        return ApiResponse.<StallResponse>builder()
                .result(stallService.create(stallCreateRequest))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StallResponse> getById(@PathVariable Long id) {
        return ApiResponse.<StallResponse>builder()
                .result(stallService.getById(id))
                .build();
    }

    @GetMapping("/street/{streetId}")
    public ApiResponse<List<StallResponse>> getByStreet(@PathVariable Long streetId) {
        return ApiResponse.<List<StallResponse>>builder()
                .result(stallService.getByStreet(streetId))
                .build();
    }

    @GetMapping("/vendor/{vendorId}")
    public ApiResponse<StallResponse> getByVendor(@PathVariable Long vendorId) {
        return ApiResponse.<StallResponse>builder()
                .result(stallService.getByVendor(vendorId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<StallResponse>> getAllActive() {
        return ApiResponse.<List<StallResponse>>builder()
                .result(stallService.getAllActive())
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<StallResponse>> getAll() {
        return ApiResponse.<List<StallResponse>>builder()
                .result(stallService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<StallResponse> update(@PathVariable Long id, @Valid @RequestBody StallCreateRequest stallCreateRequest) {

        return ApiResponse.<StallResponse>builder()
                .result(stallService.update(id, stallCreateRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        stallService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Successfully")
                .build();
    }
}