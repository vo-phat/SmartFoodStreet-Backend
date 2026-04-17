package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vendor/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VendorDashboardController {

    private final VisitEventRepository visitEventRepository;

    @GetMapping("/stats/{stallId}")
    public ApiResponse<Map<String, Object>> getStats(
            @PathVariable Long stallId,
            @RequestParam(defaultValue = "7") int days) {
        
        System.out.println("Vendor Analytics Request received for stallId: " + stallId);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);

        Map<String, Object> stats = new HashMap<>();

        // Total Visits (Enter Geofence)
        stats.put("totalVisits", visitEventRepository.countByStallAndTypeBetween(
                stallId, VisitEvent.EventType.ENTER_GEOFENCE, start, end));

        // Audio Completes
        stats.put("audioCompletes", visitEventRepository.countByStallAndTypeBetween(
                stallId, VisitEvent.EventType.AUDIO_COMPLETE, start, end));

        // QR Scans
        stats.put("qrScans", visitEventRepository.countByStallAndTypeBetween(
                stallId, VisitEvent.EventType.QR_SCAN, start, end));

        // Daily visits for chart
        List<Object[]> dailyData = visitEventRepository.findDailyVisitsByStall(stallId, start, end);
        stats.put("dailyVisits", dailyData.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("day", d[0]);
            map.put("count", d[1]);
            return map;
        }).collect(Collectors.toList()));

        return ApiResponse.<Map<String, Object>>builder()
                .result(stats)
                .build();
    }
}
