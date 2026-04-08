package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.entity.VisitEvent;
import SmartFoodStreet_Backend.entity.VisitEvent.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "qrCode", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "hour", ignore = true)
    @Mapping(target = "day", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "year", ignore = true)
    VisitEvent toEvent(Long sessionId,
                       Long stallId,
                       EventType eventType);
}