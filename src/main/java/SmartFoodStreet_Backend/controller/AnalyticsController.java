package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.enums.VisitEventType;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.service.AnalyticsRealtimeService;
import SmartFoodStreet_Backend.service.AnalyticsService;
import SmartFoodStreet_Backend.service.VisitEventAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    private final AnalyticsRealtimeService
            analyticsRealtimeService;

    private final VisitEventAsyncService
            visitEventAsyncService;

    private final StallRepository stallRepository;

    // =====================================================
    // AUDIO PLAY
    // =====================================================

    @PostMapping("/audio/{stallId}/play")
    public void audioPlay(
            @PathVariable Long stallId,
            @RequestParam String deviceId
    ) {

        // realtime redis
        analyticsService.increaseAudio(
                stallId
        );

        // mysql backup
        VisitEvent event =
                VisitEvent.builder()

                        .deviceId(deviceId)

                        .stallId(stallId)

                        .eventType(
                                VisitEventType.AUDIO_PLAY
                        )

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .build();

        visitEventAsyncService
                .logEventAsync(event);

        // realtime websocket
        analyticsRealtimeService
                .pushRealtime();
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {

        Map<String, Object> result =
                new HashMap<>();

        // =====================================================
        // total qr
        // =====================================================

        result.put(
                "totalQrScans",
                analyticsService.getTotalQr()
        );

        // =====================================================
        // unique visitors
        // =====================================================

        result.put(
                "uniqueVisitors",
                analyticsService
                        .getUniqueHomeVisitors()
        );

        // =====================================================
        // audio per stall
        // =====================================================

        List<Map<String, Object>> audioPerStall =
                new ArrayList<>();

        List<Stall> stalls =
                stallRepository.findAll();

        for (Stall stall : stalls) {

            Map<String, Object> item =
                    new HashMap<>();

            item.put(
                    "stallId",
                    stall.getId()
            );

            item.put(
                    "stallName",
                    stall.getName()
            );

            item.put(
                    "audioCount",
                    analyticsService.getAudio(
                            stall.getId()
                    )
            );

            audioPerStall.add(item);
        }

        result.put(
                "audioPerStall",
                audioPerStall
        );

        return result;
    }
}