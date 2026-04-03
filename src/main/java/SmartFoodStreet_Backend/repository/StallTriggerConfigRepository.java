package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.StallTriggerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StallTriggerConfigRepository extends JpaRepository<StallTriggerConfig, Long> {
    Optional<StallTriggerConfig> findByStallId(Long stallId);
}