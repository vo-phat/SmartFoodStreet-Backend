package SmartFoodStreet_Backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsRealtimeService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AnalyticsService analyticsService;

    @Scheduled(fixedRate = 2000)
    public void pushRealtime() {
        messagingTemplate.convertAndSend(
                "/topic/analytics",
                analyticsService.getRealtimeStats()
        );
    }
}