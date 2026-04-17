package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.service.VisitEventAsyncService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/visit")
@RequiredArgsConstructor
public class VisitController {

    private final VisitEventAsyncService visitEventAsyncService;

    @PostMapping("/log")
    public ApiResponse<Void> logVisit(
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {

        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        VisitEvent event = VisitEvent.builder()
                .eventType(VisitEvent.EventType.WEBSITE_VISIT)
                .eventTime(LocalDateTime.now())
                .ipAddress(ip)
                .userAgent(userAgent)
                .sessionId(sessionId != null && !sessionId.isEmpty() ? Long.valueOf(sessionId) : null)
                .build();

        visitEventAsyncService.logEventAsync(event);

        return ApiResponse.<Void>builder()
                .message("Visit logged")
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
