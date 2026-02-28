package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StallRepository extends JpaRepository<Stall, Long> {

    List<Stall> findByVendorId(Long vendorId);

    List<Stall> findByStreetIdAndIsActiveTrue(Long streetId);

    List<Stall> findByIsActiveTrue();
}