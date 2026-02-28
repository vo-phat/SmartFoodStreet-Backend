package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.entity.VisitEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTime",
            expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    VisitEvent toEvent(Long sessionId,
                       Long stallId,
                       VisitEvent.EventType eventType);
}