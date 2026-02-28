package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.StallStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StallStatisticsRepository extends JpaRepository<StallStatistics, Long> {

    // đảm bảo record tồn tại
    @Modifying
    @Query(value = """
                INSERT INTO stall_statistics(stall_id, total_visits, total_audio_complete, total_voucher_redeemed)
                VALUES(:stallId, 0, 0, 0)
                ON DUPLICATE KEY UPDATE stall_id = stall_id
            """, nativeQuery = true)
    void ensureExists(@Param("stallId") Long stallId);

    // atomic increment visit
    @Modifying
    @Query("""
                UPDATE StallStatistics s
                SET s.totalVisits = s.totalVisits + 1
                WHERE s.stallId = :stallId
            """)
    void incrementVisit(@Param("stallId") Long stallId);

    // atomic increment audio
    @Modifying
    @Query("""
                UPDATE StallStatistics s
                SET s.totalAudioComplete = s.totalAudioComplete + 1
                WHERE s.stallId = :stallId
            """)
    void incrementAudioComplete(@Param("stallId") Long stallId);

    // atomic increment voucher
    @Modifying
    @Query("""
                UPDATE StallStatistics s
                SET s.totalVoucherRedeemed = s.totalVoucherRedeemed + 1
                WHERE s.stallId = :stallId
            """)
    void incrementVoucherRedeemed(@Param("stallId") Long stallId);
}