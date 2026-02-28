package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.analytics.response.*;
import SmartFoodStreet_Backend.service.interfaces.IAnalytics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final IAnalytics service;

    @GetMapping("/admin/overview")
    public ApiResponse<AdminOverviewResponse> overview(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {

        return ApiResponse.<AdminOverviewResponse>builder()
                .result(service.adminOverview(start, end))
                .build();
    }

    @GetMapping("/vendor/{stallId}")
    public ApiResponse<VendorDashboardResponse> vendor(
            @PathVariable Long stallId) {

        return ApiResponse.<VendorDashboardResponse>builder()
                .result(service.vendorDashboard(stallId))
                .build();
    }

    @GetMapping("/admin/street/{streetId}")
    public ApiResponse<StreetDashboardResponse> street(
            @PathVariable Long streetId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {

        return ApiResponse.<StreetDashboardResponse>builder()
                .result(service.streetDashboard(streetId, start, end))
                .build();
    }

    @GetMapping("/admin/top-stalls")
    public ApiResponse<List<TopStallResponse>> topStalls() {

        return ApiResponse.<List<TopStallResponse>>builder()
                .result(service.topStalls())
                .build();
    }

    @GetMapping("/admin/heatmap")
    public ApiResponse<List<HeatmapPointResponse>> heatmap(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {

        return ApiResponse.<List<HeatmapPointResponse>>builder()
                .result(service.heatmap(start, end))
                .build();
    }
}