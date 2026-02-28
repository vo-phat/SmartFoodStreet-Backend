package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.dto.analytics.response.*;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.StallStatistics;
import SmartFoodStreet_Backend.repository.*;
import SmartFoodStreet_Backend.service.interfaces.IAnalytics;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService implements IAnalytics {

    private final VisitSessionRepository sessionRepo;
    private final VisitEventRepository eventRepo;
    private final VoucherRepository voucherRepo;
    private final StallStatisticsRepository statsRepo;
    private final LocationLogRepository locationRepo;
    private final StallRepository stallRepo;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AdminOverviewResponse adminOverview(LocalDateTime start, LocalDateTime end) {

        Long sessions = sessionRepo.countByStartedAtBetween(start, end);

        Long enter = eventRepo.countEnterBetween(start, end);

        Long audio = eventRepo.countAudioCompleteBetween(start, end);

        Long generated = voucherRepo.countGeneratedBetween(start, end);

        Long redeemed = voucherRepo.countRedeemedBetween(start, end);

        double redemptionRate =
                generated == 0 ? 0 :
                        (double) redeemed / generated;

        double audioRate =
                enter == 0 ? 0 :
                        (double) audio / enter;

        return AdminOverviewResponse.builder()
                .totalSessions(sessions)
                .totalEnterEvents(enter)
                .totalAudioComplete(audio)
                .totalVoucherGenerated(generated)
                .totalVoucherRedeemed(redeemed)
                .redemptionRate(redemptionRate)
                .audioCompletionRate(audioRate)
                .estimatedRevenueImpact(redeemed * 20000)
                .build();
    }

    @Override
    @PreAuthorize("hasRole('VENDOR')")
    public VendorDashboardResponse vendorDashboard(Long stallId) {

        StallStatistics stats = statsRepo.findById(stallId).orElseThrow();

        double conversion =
                stats.getTotalVisits() == 0 ? 0 :
                        (double) stats.getTotalVoucherRedeemed()
                                / stats.getTotalVisits();

        return VendorDashboardResponse.builder()
                .stallId(stallId)
                .totalVisits(stats.getTotalVisits())
                .totalAudioComplete(stats.getTotalAudioComplete())
                .totalVoucherRedeemed(stats.getTotalVoucherRedeemed())
                .conversionRate(conversion)
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public StreetDashboardResponse streetDashboard(Long streetId, LocalDateTime start, LocalDateTime end) {

        Long sessions = sessionRepo.countByStreetIdAndStartedAtBetween(streetId, start, end);

        Long enter = eventRepo.countEnterBetween(start, end);

        Long redeemed = voucherRepo.countRedeemedBetween(start, end);

        return StreetDashboardResponse.builder()
                .streetId(streetId)
                .totalSessions(sessions)
                .totalEnterEvents(enter)
                .totalVoucherRedeemed(redeemed)
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopStallResponse> topStalls() {

        return eventRepo.findTopStallsByVisits()
                .stream()
                .limit(5)
                .map(obj -> {

                    Long stallId = (Long) obj[0];
                    Long count = (Long) obj[1];

                    Stall stall = stallRepo.findById(stallId).orElseThrow();

                    return TopStallResponse.builder()
                            .stallId(stallId)
                            .stallName(stall.getName())
                            .totalVisits(count.intValue())
                            .totalVoucherRedeemed(
                                    statsRepo.findById(stallId)
                                            .map(StallStatistics::getTotalVoucherRedeemed)
                                            .orElse(0))
                            .build();
                })
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<HeatmapPointResponse> heatmap(LocalDateTime start, LocalDateTime end) {

        return locationRepo.heatmap(start, end)
                .stream()
                .map(obj -> HeatmapPointResponse.builder()
                        .latitude((Double) obj[0])
                        .longitude((Double) obj[1])
                        .count((Long) obj[2])
                        .build())
                .toList();
    }
}