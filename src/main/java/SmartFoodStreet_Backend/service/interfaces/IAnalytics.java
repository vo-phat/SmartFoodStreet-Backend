package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.analytics.response.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IAnalytics {

    AdminOverviewResponse adminOverview(LocalDateTime start, LocalDateTime end);

    VendorDashboardResponse vendorDashboard(Long stallId);

    StreetDashboardResponse streetDashboard(Long streetId, LocalDateTime start, LocalDateTime end);

    List<TopStallResponse> topStalls();

    List<HeatmapPointResponse> heatmap(LocalDateTime start, LocalDateTime end);
}