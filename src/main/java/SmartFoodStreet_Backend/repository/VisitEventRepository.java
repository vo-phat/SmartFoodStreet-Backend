package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.VisitEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitEventRepository extends JpaRepository<VisitEvent, Long> {

    Optional<VisitEvent> findTopBySessionIdAndStallIdAndEventTypeOrderByEventTimeDesc(
            Long sessionId,
            Long stallId,
            VisitEvent.EventType eventType);

    boolean existsByQrCodeAndIpAddressAndEventTimeAfter(
            String code,
            String ip,
            LocalDateTime time);

    @Query("""
                SELECT COUNT(e)
                FROM VisitEvent e
                WHERE e.eventType = 'ENTER_GEOFENCE'
                AND e.eventTime BETWEEN :start AND :end
            """)
    Long countEnterBetween(LocalDateTime start,
            LocalDateTime end);

    @Query("""
                SELECT COUNT(e)
                FROM VisitEvent e
                WHERE e.eventType = 'AUDIO_COMPLETE'
                AND e.eventTime BETWEEN :start AND :end
            """)
    Long countAudioCompleteBetween(LocalDateTime start,
            LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT e.ipAddress) FROM VisitEvent e")
    Long countUniqueVisitors();

    @Query("SELECT COUNT(e) FROM VisitEvent e WHERE e.eventType = 'WEBSITE_VISIT' OR e.eventType = 'QR_SCAN'")
    Long countTotalVisits();

    @Query("""
                SELECT e.stallId, COUNT(e)
                FROM VisitEvent e
                WHERE e.eventType = 'ENTER_GEOFENCE'
                GROUP BY e.stallId
                ORDER BY COUNT(e) DESC
            """)
    List<Object[]> findTopStallsByVisits();
}
