package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/realtime")
    public ApiResponse<?> realtime() {
        return ApiResponse.builder()
                .result(analyticsService.getRealtimeStats())
                .build();
    }

    @GetMapping("/stall-audio")
    public ApiResponse<?> audioStats() {
        return ApiResponse.builder()
                .result(analyticsService.getAudioStats())
                .build();
    }
}