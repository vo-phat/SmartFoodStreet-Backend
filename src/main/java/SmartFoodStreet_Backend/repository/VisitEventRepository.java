package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.enums.VisitEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitEventRepository extends JpaRepository<VisitEvent, Long> {

    long countByEventTypeIn(List<VisitEventType> eventTypes);

    @Query("""
                SELECT COUNT(DISTINCT v.deviceId)
                FROM VisitEvent v
                WHERE v.eventType = 'HOME_QR_SCAN'
            """)
    long countUniqueHomeVisitors();

    boolean existsByDeviceIdAndEventTypeAndCreatedAtAfter(String deviceId, VisitEventType eventType, LocalDateTime createdAt);

    Long countByStallIdAndEventTypeAndCreatedAtBetween(Long stallId, VisitEventType visitEventType, LocalDateTime dayStart, LocalDateTime dayEnd);

}