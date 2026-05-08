package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.enums.VisitEventType;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import SmartFoodStreet_Backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/vendor/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VendorDashboardController {

    private final VisitEventRepository
            visitEventRepository;

    private final AnalyticsService
            analyticsService;

    // =====================================================
    // VENDOR STATS
    // =====================================================

    @GetMapping("/stats/{stallId}")
    public ApiResponse<Map<String, Object>> getStats(
            @PathVariable Long stallId,
            @RequestParam(defaultValue = "7") int days
    ) {

        LocalDateTime end =
                LocalDateTime.now();

        LocalDateTime start =
                end.minusDays(days);

        Map<String, Object> stats =
                new HashMap<>();

        // =====================================================
        // total qr scans
        // =====================================================

        Long qrScans =
                visitEventRepository.countByStallIdAndEventTypeAndCreatedAtBetween(
                        stallId,
                        VisitEventType.STALL_QR_SCAN,
                        start,
                        end
                );

        stats.put(
                "qrScans",
                qrScans
        );

        // =====================================================
        // audio plays
        // =====================================================

        Long audioPlays =
                visitEventRepository.countByStallIdAndEventTypeAndCreatedAtBetween(
                        stallId,
                        VisitEventType.AUDIO_PLAY,
                        start,
                        end
                );

        stats.put(
                "audioPlays",
                audioPlays
        );

        // =====================================================
        // realtime audio counter
        // =====================================================

        stats.put(
                "realtimeAudio",
                analyticsService.getAudio(
                        stallId
                )
        );

        // =====================================================
        // daily audio chart
        // =====================================================

        List<Map<String, Object>> chart =
                new ArrayList<>();

        LocalDate current =
                start.toLocalDate();

        while (
                !current.isAfter(
                        end.toLocalDate()
                )
        ) {

            LocalDateTime dayStart =
                    current.atStartOfDay();

            LocalDateTime dayEnd =
                    current.atTime(
                            23,
                            59,
                            59
                    );

            Long count =
                    visitEventRepository
                            .countByStallIdAndEventTypeAndCreatedAtBetween(
                                    stallId,
                                    VisitEventType.AUDIO_PLAY,
                                    dayStart,
                                    dayEnd
                            );

            Map<String, Object> item =
                    new HashMap<>();

            item.put(
                    "date",
                    current.toString()
            );

            item.put(
                    "audioCount",
                    count
            );

            chart.add(item);

            current =
                    current.plusDays(1);
        }

        stats.put(
                "audioChart",
                chart
        );

        return ApiResponse
                .<Map<String, Object>>builder()

                .result(stats)

                .build();
    }
}