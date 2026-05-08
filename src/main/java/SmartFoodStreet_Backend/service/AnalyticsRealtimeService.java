package SmartFoodStreet_Backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsRealtimeService {

    private final AnalyticsService analyticsService;

    private final SimpMessagingTemplate messagingTemplate;

    public void pushRealtime() {

        try {

            Map<String, Object> payload =
                    new HashMap<>();

            payload.put(
                    "totalQrScans",
                    analyticsService.getTotalQr()
            );

            payload.put(
                    "uniqueHomeVisitors",
                    analyticsService.getUniqueHomeVisitors()
            );

            messagingTemplate.convertAndSend(
                    "/topic/analytics",
                    payload
            );

        } catch (Exception e) {

            log.error(
                    "Realtime websocket push failed",
                    e
            );
        }
    }
}