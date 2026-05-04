package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.entity.AnalyticsDaily;
import SmartFoodStreet_Backend.repository.AnalyticsDailyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsSyncService {

    private final StringRedisTemplate redis;
    private final AnalyticsDailyRepository repository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void sync() {
        LocalDate today = LocalDate.now();

        // 1. ĐỒNG BỘ LƯỢT QUÉT VÀO /HOME (GATEWAY)
        // Đây chính là cái ông cần để hiện Dashboard: stall_id = null
        syncValue(today, null, "analytics:general:visits", "visits");

        // 2. ĐỒNG BỘ CHO TẤT CẢ GIAN HÀNG ĐANG CÓ TRONG REDIS (KHÔNG CẦN LOOP 1-50)
        // Ta lấy tất cả các key liên quan đến audio hoặc visits của stall
        Set<String> audioKeys = scanKeys("analytics:stall:*:audio");
        if (audioKeys != null) {
            for (String key : audioKeys) {
                Long stallId = extractStallId(key);
                syncValue(today, stallId, key, "audio");
            }
        }

        Set<String> visitKeys = redis.keys("analytics:stall:*:visits");
        if (visitKeys != null) {
            for (String key : visitKeys) {
                Long stallId = extractStallId(key);
                syncValue(today, stallId, key, "visits");
            }
        }
    }

    // HÀM HELPER ĐỂ TRÍCH XUẤT ID (GIẢI QUYẾT LỖI TRONG ẢNH)
    private Long extractStallId(String key) {
        try {
            // key: "analytics:stall:12:audio" -> tách chuỗi lấy phần tử index 2
            String[] parts = key.split(":");
            return Long.parseLong(parts[2]);
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất stallId từ key: {}", key);
            return null;
        }
    }

    // HÀM ĐỒNG BỘ GIÁ TRỊ VÀO DATABASE
    private void syncValue(LocalDate today, Long stallId, String redisKey, String type) {
        String value = redis.opsForValue().get(redisKey);
        int count = (value != null) ? Integer.parseInt(value) : 0;

        // Tìm bản ghi cũ hoặc tạo mới nếu chưa có (JPA xử lý stallId = null cực chuẩn)
        AnalyticsDaily entity = repository.findByDateAndStallId(today, stallId)
                .orElse(AnalyticsDaily.builder()
                        .date(today)
                        .stallId(stallId)
                        .totalVisits(0)
                        .totalAudioPlays(0)
                        .build());

        if ("audio".equals(type)) {
            entity.setTotalAudioPlays(count);
        } else if ("visits".equals(type)) {
            entity.setTotalVisits(count);
        }

        repository.save(entity);
    }

    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();

        redis.execute((RedisCallback<Void>) conn -> {
            Cursor<byte[]> cursor = conn.scan(options);
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            return null;
        });

        return keys;
    }
}