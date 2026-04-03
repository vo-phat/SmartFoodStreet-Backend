package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StallRepository extends JpaRepository<Stall, Long> {

    List<Stall> findByVendorId(Long vendorId);

    List<Stall> findByStreetIdAndIsActiveTrue(Long streetId);

    List<Stall> findByIsActiveTrue();

    List<Stall> findByStreetId(Long streetId);

    boolean existsByStreetIdAndNameIgnoreCase(Long streetId, String name);

    @Query(value = """
            SELECT s.*,
                   (6371000 * acos(
                       cos(radians(:lat)) *
                       cos(radians(s.latitude)) *
                       cos(radians(s.longitude) - radians(:lng)) +
                       sin(radians(:lat)) *
                       sin(radians(s.latitude))
                   )) AS distance
            FROM stalls s
            HAVING distance <= :radius
            ORDER BY distance
            """, nativeQuery = true)
    List<Stall> findNearbyStalls(double lat, double lng, double radius);
}