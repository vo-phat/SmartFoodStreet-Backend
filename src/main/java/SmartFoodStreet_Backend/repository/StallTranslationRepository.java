package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.StallTranslation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StallTranslationRepository extends JpaRepository<StallTranslation, Long> {
    Optional<Object> findByStallIdAndLanguageCode(Long stallId, String lang);

    boolean existsByStallIdAndLanguageCode(@NotNull Long stallId, @NotBlank String languageCode);
}