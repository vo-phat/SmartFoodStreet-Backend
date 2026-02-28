package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.gps.request.GpsCheckRequest;
import SmartFoodStreet_Backend.dto.gps.response.GpsCheckResponse;
import SmartFoodStreet_Backend.entity.Stall;
import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.entity.VisitSession;
import SmartFoodStreet_Backend.mapper.GpsMapper;
import SmartFoodStreet_Backend.mapper.VisitEventMapper;
import SmartFoodStreet_Backend.repository.StallRepository;
import SmartFoodStreet_Backend.repository.VisitEventRepository;
import SmartFoodStreet_Backend.repository.VisitSessionRepository;
import SmartFoodStreet_Backend.service.interfaces.IGps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GpsService implements IGps {

    private final VisitSessionRepository sessionRepository;
    private final StallRepository stallRepository;
    private final VisitEventRepository eventRepository;

    private final GpsMapper gpsMapper;
    private final VisitEventMapper eventMapper;

    @Override
    public GpsCheckResponse check(GpsCheckRequest request) {

        VisitSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Session đã kết thúc thì không xử lý
        if (session.getEndedAt() != null)
            return gpsMapper.empty();

        List<Stall> stalls =
                stallRepository.findByStreetIdAndIsActiveTrue(
                        session.getStreet().getId());

        Stall bestMatch = null;
        double bestDistance = Double.MAX_VALUE;

        for (Stall stall : stalls) {

            double distance = calculateDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    stall.getLatitude(),
                    stall.getLongitude()
            );

            if (distance > stall.getRadius())
                continue;

            if (isCooldownActive(session.getId(), stall))
                continue;

            if (bestMatch == null
                    || stall.getPriority() > bestMatch.getPriority()
                    || (stall.getPriority().equals(bestMatch.getPriority())
                    && distance < bestDistance)) {

                bestMatch = stall;
                bestDistance = distance;
            }
        }

        if (bestMatch == null)
            return gpsMapper.empty();

        VisitEvent event = eventMapper.toEvent(
                session.getId(),
                bestMatch.getId(),
                VisitEvent.EventType.ENTER_GEOFENCE
        );

        eventRepository.save(event);

        return gpsMapper.toResponse(bestMatch);
    }

    private boolean isCooldownActive(Long sessionId, Stall stall) {

        Optional<VisitEvent> lastEvent =
                eventRepository
                        .findTopBySessionIdAndStallIdAndEventTypeOrderByEventTimeDesc(
                                sessionId,
                                stall.getId(),
                                VisitEvent.EventType.ENTER_GEOFENCE
                        );

        if (lastEvent.isEmpty())
            return false;

        long seconds = Duration.between(
                lastEvent.get().getEventTime().toInstant(),
                Instant.now()
        ).getSeconds();

        return seconds < stall.getCooldownSeconds();
    }

    // Haversine Formula (mét)
    private double calculateDistance(double lat1, double lon1,
                                     double lat2, double lon2) {

        final int EARTH_RADIUS = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}