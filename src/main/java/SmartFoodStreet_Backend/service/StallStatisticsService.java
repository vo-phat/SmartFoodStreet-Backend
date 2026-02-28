package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.repository.StallStatisticsRepository;
import SmartFoodStreet_Backend.service.interfaces.IStallStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StallStatisticsService implements IStallStatistics {

    private final StallStatisticsRepository statsRepo;

    @Transactional
    public void increaseVisit(Long stallId) {

        statsRepo.ensureExists(stallId);
        statsRepo.incrementVisit(stallId);
    }

    @Transactional
    public void increaseAudioComplete(Long stallId) {

        statsRepo.ensureExists(stallId);
        statsRepo.incrementAudioComplete(stallId);
    }

    @Transactional
    public void increaseVoucherRedeemed(Long stallId) {

        statsRepo.ensureExists(stallId);
        statsRepo.incrementVoucherRedeemed(stallId);
    }
}
