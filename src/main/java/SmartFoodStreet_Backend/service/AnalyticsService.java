package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.service.interfaces.IAnalytics;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService implements IAnalytics {

    private final StringRedisTemplate redis;

    // ===== KEY CHUẨN =====
    private static final String WEB_TOTAL = "analytics:web:total";
    private static final String WEB_UNIQUE = "analytics:web:unique";

    private static final String GATEWAY_TOTAL = "analytics:gateway:total";

    // =====================

    @Override
    public void increaseVisit(String sessionId) {

        // 1. Tổng toàn hệ thống
        redis.opsForValue().increment(WEB_TOTAL);

        // 2. Gateway (homepage / QR gateway)
        redis.opsForValue().increment(GATEWAY_TOTAL);

        // 3. Online user REAL (TTL-based)
        if (sessionId != null && !sessionId.isBlank()) {
            redis.opsForValue().set(
                    "session:" + sessionId,
                    "1",
                    Duration.ofMinutes(5) // 🔥 auto expire
            );
        }
    }

    @Override
    public void increaseAudio(Long stallId) {

    }

    @Override
    public void increaseAudio(Long stallId, String sessionId) {
        String key = "analytics:stall:" + stallId + ":audio";
        String uniqueKey = key + ":unique";

        redis.opsForValue().increment(key);

        if (sessionId != null) {
            redis.opsForSet().add(uniqueKey, sessionId);
        }
    }

    @Override
    public void increaseStallVisit(Long stallId) {
        redis.opsForValue().increment("analytics:stall:" + stallId + ":visits");
    }

    @Override
    public long getOnlineUsers() {
//        Long size = redis.opsForSet().size(WEB_UNIQUE);
//        return size != null ? size : 0;
        return scanKeys("session:*").size();
    }

    @Override
    public Map<String, Object> getRealtimeStats() {
        Map<String, Object> map = new HashMap<>();

        map.put("totalVisits", getInt(WEB_TOTAL));
        map.put("gatewayVisits", getInt(GATEWAY_TOTAL));
        map.put("onlineUsers", getOnlineUsers());

        return map;
    }

    @Override
    public Map<Long, Integer> getAudioStats() {

        Map<Long, Integer> result = new LinkedHashMap<>();

        Set<String> keys = scanKeys("analytics:stall:*:audio");

        for (String key : keys) {
            Long stallId = extractStallId(key);
            int value = getInt(key);
            result.put(stallId, value);
        }

        return result;
    }

    @Override
    public Map<Long, Integer> getStallVisitStats() {
        return Map.of();
    }

    @Override
    public void removeSession(String sessionId) {

    }

    // ===== HELPER =====

    private int getInt(String key) {
        String val = redis.opsForValue().get(key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    private Long extractStallId(String key) {
        try {
            return Long.parseLong(key.split(":")[2]);
        } catch (Exception e) {
            return null;
        }
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