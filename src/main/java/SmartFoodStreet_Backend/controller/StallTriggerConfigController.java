package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.stall.request.StallTriggerConfigRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallTriggerConfigResponse;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.service.VisitEventAsyncService;
import SmartFoodStreet_Backend.service.interfaces.IStallTriggerConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/stall-trigger-config")
@RequiredArgsConstructor
public class StallTriggerConfigController {

    private final IStallTriggerConfig configService;
    private final VisitEventAsyncService visitEventAsyncService;

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

    @PostMapping("/log-visit")
    public ApiResponse<Void> logVisit(
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        System.out.println("Nhận được logVisit request qua StallTriggerConfigController. SessionId: " + sessionId);

        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        LocalDateTime now = LocalDateTime.now();
        VisitEvent event = VisitEvent.builder()
                .eventType(VisitEvent.EventType.WEBSITE_VISIT)
                .eventTime(now)
                .ipAddress(ip)
                .userAgent(userAgent)
                .sessionId(sessionId != null && !sessionId.isEmpty() ? Long.valueOf(sessionId) : null)
                .hour(now.getHour())
                .day(now.getDayOfMonth())
                .month(now.getMonthValue())
                .year(now.getYear())
                .build();

        visitEventAsyncService.logEventAsync(event);

        return ApiResponse.<Void>builder()
                .message("Visit logged via combined controller")
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null || "".equals(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }
}