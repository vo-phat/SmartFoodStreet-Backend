package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.enums.VisitEventType;
import SmartFoodStreet_Backend.service.AnalyticsRealtimeService;
import SmartFoodStreet_Backend.service.AnalyticsService;
import SmartFoodStreet_Backend.service.VisitEventAsyncService;
import SmartFoodStreet_Backend.service.interfaces.IStallTriggerConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/stall-trigger-config")
@RequiredArgsConstructor
public class StallTriggerConfigController {

    private final IStallTriggerConfig
            configService;

    private final VisitEventAsyncService
            visitEventAsyncService;

    private final AnalyticsService
            analyticsService;

    private final AnalyticsRealtimeService
            analyticsRealtimeService;

    // =====================================================
    // CREATE / UPDATE CONFIG
    // =====================================================

    @PostMapping
    public ApiResponse<Void> createOrUpdate(
            @Valid
            @RequestBody
            StallTriggerConfigRequest req
    ) {

        configService.createOrUpdate(req);

        return ApiResponse.<Void>builder()
                .message("Successfully")
                .build();
    }

    // =====================================================
    // GET CONFIG
    // =====================================================

    @GetMapping("/{stallId}")
    public ApiResponse<StallTriggerConfigResponse>
    getByStall(
            @PathVariable Long stallId
    ) {

        return ApiResponse
                .<StallTriggerConfigResponse>builder()

                .result(
                        configService.getByStall(
                                stallId
                        )
                )

                .build();
    }

    // =====================================================
    // LOG HOME VISIT
    // =====================================================

    @PostMapping("/log-visit")
    public ApiResponse<Void> logVisit(
            @RequestParam String deviceId
    ) {

        // =====================================================
        // realtime analytics
        // =====================================================

        analyticsService
                .increaseTotalQr();

        analyticsService
                .addUniqueHomeVisitor(
                        deviceId
                );

        // =====================================================
        // mysql backup
        // =====================================================

        VisitEvent event =
                VisitEvent.builder()

                        .deviceId(deviceId)

                        .eventType(
                                VisitEventType.HOME_QR_SCAN
                        )

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .build();

        visitEventAsyncService
                .logEventAsync(event);

        // =====================================================
        // websocket realtime
        // =====================================================

        analyticsRealtimeService
                .pushRealtime();

        return ApiResponse.<Void>builder()

                .message(
                        "Visit logged successfully"
                )

                .build();
    }
}