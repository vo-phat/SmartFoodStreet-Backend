package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallCreationRequest;
import SmartFoodStreet_Backend.dto.stall.request.StallUpdateRequest;
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

    private final IStall service;

    // ================= VENDOR =================

    @PostMapping
    public ApiResponse<StallResponse> create(
            @RequestBody @Valid StallCreationRequest request) {
        return ApiResponse.<StallResponse>builder()
                .result(service.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<StallResponse> update(
            @PathVariable Long id,
            @RequestBody StallUpdateRequest request) {
        return ApiResponse.<StallResponse>builder()
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

    @GetMapping("/my")
    public ApiResponse<List<StallResponse>> myStalls() {
        return ApiResponse.<List<StallResponse>>builder()
                .result(service.getMyStalls())
                .build();
    }

    // ================= PUBLIC =================

    @GetMapping
    public ApiResponse<List<StallResponse>> byStreet(
            @RequestParam Long streetId) {
        return ApiResponse.<List<StallResponse>>builder()
                .result(service.getByStreet(streetId))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StallResponse> getById(@PathVariable Long id) {
        return ApiResponse.<StallResponse>builder()
                .result(service.getById(id))
                .build();
    }

    // ================= ADMIN =================

    @GetMapping("/admin")
    public ApiResponse<List<StallResponse>> getAllAdmin() {
        return ApiResponse.<List<StallResponse>>builder()
                .result(service.getAllAdmin())
                .build();
    }
}