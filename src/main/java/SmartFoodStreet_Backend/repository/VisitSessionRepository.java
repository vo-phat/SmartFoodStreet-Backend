package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.VisitSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitSessionRepository extends JpaRepository<VisitSession, Long> {

    List<VisitSession> findByStreetId(Long streetId);

    Long countByStartedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByStreetIdAndStartedAtBetween(Long streetId, LocalDateTime start, LocalDateTime end);

}