package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.FoodStreet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodStreetRepository extends JpaRepository<FoodStreet, Long> {

    List<FoodStreet> findByIsActiveTrue();

}