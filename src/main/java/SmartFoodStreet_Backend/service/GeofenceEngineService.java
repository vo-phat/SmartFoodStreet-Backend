package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.entity.VisitSession;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeofenceEngineService {

    private final StallRepository stallRepo;
    private final VisitEventRepository eventRepo;
    private final StallStatisticsService statisticsService;

    @Transactional
    public void processLocation(VisitSession session, double userLat, double userLng) {

        List<Stall> stalls = stallRepo.findByStreetIdAndIsActiveTrue(session.getStreet().getId());

        for (Stall stall : stalls) {

            double distance = haversine(
                            userLat,
                            userLng,
                            stall.getLatitude().doubleValue(),
                            stall.getLongitude().doubleValue()
                    );

            if (distance <= stall.getRadius()) {

                // log event
                VisitEvent event = VisitEvent.builder()
                        .sessionId(session.getId())
                        .stallId(stall.getId())
                        .eventType(VisitEvent.EventType.ENTER_GEOFENCE)
                        .eventTime(new Timestamp(System.currentTimeMillis()))
                        .build();

                eventRepo.save(event);

                // update statistics atomic
                statisticsService.increaseVisit(stall.getId());
            }
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) *
                                Math.cos(Math.toRadians(lat2)) *
                                Math.sin(dLon / 2) *
                                Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
