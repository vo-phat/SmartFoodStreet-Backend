package SmartFoodStreet_Backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final StringRedisTemplate redis;

    // =====================================================
    // redis keys
    // =====================================================

    private static final String TOTAL_QR =
            "analytics:total_qr";

    // =====================================================
    // total qr
    // =====================================================

    public void increaseTotalQr() {

        redis.opsForValue()
                .increment(TOTAL_QR);
    }

    public Long getTotalQr() {

        String value =
                redis.opsForValue()
                        .get(TOTAL_QR);

        return value == null
                ? 0L
                : Long.valueOf(value);
    }

    // =====================================================
    // unique visitors
    // =====================================================

    public void addUniqueHomeVisitor(
            String deviceId
    ) {

        String key =
                "analytics:home_unique:"
                        + LocalDate.now();

        redis.opsForSet()
                .add(key, deviceId);
    }

    public Long getUniqueHomeVisitors() {

        String key =
                "analytics:home_unique:"
                        + LocalDate.now();

        Long size =
                redis.opsForSet()
                        .size(key);

        return size == null
                ? 0L
                : size;
    }

    // =====================================================
    // audio
    // =====================================================

    public void increaseAudio(
            Long stallId
    ) {

        redis.opsForValue()
                .increment(
                        "analytics:stall:"
                                + stallId
                                + ":audio"
                );
    }

    public Long getAudio(
            Long stallId
    ) {

        String value =
                redis.opsForValue()
                        .get(
                                "analytics:stall:"
                                        + stallId
                                        + ":audio"
                        );

        return value == null
                ? 0L
                : Long.valueOf(value);
    }
}