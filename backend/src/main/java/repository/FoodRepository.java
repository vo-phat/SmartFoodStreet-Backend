package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByStallId(Long stallId);

    boolean existsByStallIdAndNameIgnoreCase(Long stallId, String name);
}