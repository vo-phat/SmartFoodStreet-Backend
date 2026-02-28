package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.visitsesion.request.StartSessionRequest;
import SmartFoodStreet_Backend.dto.visitsesion.request.UpdateBudgetRequest;
import SmartFoodStreet_Backend.dto.visitsesion.response.VisitSessionResponse;
import SmartFoodStreet_Backend.service.interfaces.IVisitSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class VisitSessionController {

    private final IVisitSession service;

    @PostMapping("/start")
    public ApiResponse<VisitSessionResponse> start(
            @RequestBody @Valid StartSessionRequest request) {

        return ApiResponse.<VisitSessionResponse>builder()
                .result(service.start(request))
                .build();
    }

    @PostMapping("/{id}/end")
    public ApiResponse<VisitSessionResponse> end(
            @PathVariable Long id) {

        return ApiResponse.<VisitSessionResponse>builder()
                .result(service.end(id))
                .build();
    }

    @PatchMapping("/{id}/budget")
    public ApiResponse<VisitSessionResponse> updateBudget(
            @PathVariable Long id,
            @RequestBody UpdateBudgetRequest request) {

        return ApiResponse.<VisitSessionResponse>builder()
                .result(service.updateBudget(id, request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<VisitSessionResponse> getById(
            @PathVariable Long id) {

        return ApiResponse.<VisitSessionResponse>builder()
                .result(service.getById(id))
                .build();
    }
}