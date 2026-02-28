package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.visitsesion.request.StartSessionRequest;
import SmartFoodStreet_Backend.dto.visitsesion.response.VisitSessionResponse;
import SmartFoodStreet_Backend.entity.VisitSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "street", ignore = true)
    @Mapping(target = "startedAt",
            expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "endedAt", ignore = true)
    VisitSession toEntity(StartSessionRequest request);

    @Mapping(target = "streetId", source = "street.id")
    VisitSessionResponse toResponse(VisitSession entity);
}