package backend.controller;

import backend.common.response.ApiResponse;
import backend.repository.VisitEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final VisitEventRepository visitEventRepository;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVisits", visitEventRepository.countTotalVisits());
        stats.put("uniqueVisitors", visitEventRepository.countUniqueVisitors());

        return ApiResponse.<Map<String, Object>>builder()
                .result(stats)
                .build();
    }

    @GetMapping("/audio-stats")
    public ApiResponse<List<Map<String, Object>>> getAudioStats() {
        List<Object[]> data = visitEventRepository.findAudioStatsByStall();

        List<Map<String, Object>> result = data.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("stallName", d[0]);
            map.put("count", d[1]);
            return map;
        }).collect(Collectors.toList());

        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(result)
                .build();
    }
}