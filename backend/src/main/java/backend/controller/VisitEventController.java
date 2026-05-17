package backend.controller;

import backend.common.response.ApiResponse;
import backend.dto.visitsesion.request.VisitEventRequest;
import backend.entity.VisitEvent;
import backend.repository.VisitEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/visit-events")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VisitEventController {

    private final VisitEventRepository visitEventRepository;

    @PostMapping("/log")
    public ApiResponse<String> logEvent(@RequestBody VisitEventRequest request, HttpServletRequest httpRequest) {
        LocalDateTime now = LocalDateTime.now();

        VisitEvent event = VisitEvent.builder()
                .stallId(request.getStallId())
                .sessionId(request.getSessionId())
                .eventType(VisitEvent.EventType.valueOf(request.getEventType()))
                .qrCode(request.getQrCode())
                // Lấy thông tin môi trường
                .ipAddress(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                // Tự động tách thời gian để sau này Group By trong Dashboard dễ dàng
                .eventTime(now)
                .hour(now.getHour())
                .day(now.getDayOfMonth())
                .month(now.getMonthValue())
                .year(now.getYear())
                .build();

        visitEventRepository.save(event);

        return ApiResponse.<String>builder()
                .result("Event logged successfully")
                .build();
    }
}