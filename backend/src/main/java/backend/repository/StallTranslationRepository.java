package backend.repository;

import backend.entity.StallTranslation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StallTranslationRepository extends JpaRepository<StallTranslation, Long> {
    Optional<StallTranslation> findByStallIdAndLanguageCode(Long stallId, String lang);

    boolean existsByStallIdAndLanguageCode(@NotNull Long stallId, @NotBlank String languageCode);

    List<StallTranslation> findByStallId(Long stallId);

    // Dùng cho Fallback: Lấy bản dịch đầu tiên bất kỳ của Stall nếu không tìm thấy
    // ngôn ngữ yêu cầu
    Optional<StallTranslation> findFirstByStallId(Long stallId);
 
    void deleteByStallId(Long stallId);

    @Modifying
    @Transactional
    @Query("""
UPDATE StallTranslation s
SET s.audioStatus = 'PROCESSING'
WHERE s.id = :id
AND (
    s.audioStatus = 'PENDING'
    OR s.audioStatus = 'ERROR'
)
""")
    int markAsProcessing(Long id);
}