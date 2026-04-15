package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.QRCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findByCode(String code);

    @Query("SELECT q FROM QRCode q JOIN FETCH q.stall WHERE q.code = :code")
    Optional<QRCode> findByCodeWithStall(@Param("code") String code);

    Optional<QRCode> findByStallId(Long stallId);

    @Modifying
    @Query("UPDATE QRCode q SET q.scanCount = q.scanCount + 1 WHERE q.id = :id")
    void incrementScanCount(@Param("id") Long id);
}
