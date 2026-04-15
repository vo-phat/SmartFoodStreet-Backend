package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.stall.request.StallCreateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;
import SmartFoodStreet_Backend.entity.Stall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StallMapper {

    Stall toEntity(StallCreateRequest request);

    @Mapping(target = "radius", source = "triggerConfig.radius")
    StallResponse toResponse(Stall stall);
}