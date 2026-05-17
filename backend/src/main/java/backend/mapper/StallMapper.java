package backend.mapper;

import backend.dto.stall.request.StallCreateRequest;
import backend.dto.stall.response.StallResponse;
import backend.entity.Stall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StallMapper {

    Stall toEntity(StallCreateRequest request);

    @Mapping(target = "radius", source = "triggerConfig.radius")
    StallResponse toResponse(Stall stall);
}