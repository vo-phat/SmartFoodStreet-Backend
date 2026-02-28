package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationLogRepository
        extends JpaRepository<LocationLog, Long> {

    @Query("""
                SELECT l.latitude, l.longitude, COUNT(l)
                FROM LocationLog l
                WHERE l.recordedAt BETWEEN :start AND :end
                GROUP BY l.latitude, l.longitude
            """)
    List<Object[]> heatmap(LocalDateTime start, LocalDateTime end);
}