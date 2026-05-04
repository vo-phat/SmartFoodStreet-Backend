package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.AnalyticsDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AnalyticsDailyRepository extends JpaRepository<AnalyticsDaily, Long> {

    Optional<AnalyticsDaily> findByDateAndStallId(LocalDate date, Long stallId);
}