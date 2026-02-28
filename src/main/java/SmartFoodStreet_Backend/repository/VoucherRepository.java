package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VoucherRepository
        extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findByVoucherCode(String voucherCode);

    Optional<Voucher> findBySessionIdAndStallIdAndIsRedeemedFalse(
            Long sessionId,
            Long stallId
    );

    @Query("""
                SELECT COUNT(v)
                FROM Voucher v
                WHERE v.createdAt BETWEEN :start AND :end
            """)
    Long countGeneratedBetween(LocalDateTime start,
                               LocalDateTime end);

    @Query("""
                SELECT COUNT(v)
                FROM Voucher v
                WHERE v.isRedeemed = true
                AND v.redeemedAt BETWEEN :start AND :end
            """)
    Long countRedeemedBetween(LocalDateTime start,
                              LocalDateTime end);
}