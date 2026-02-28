package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.gps.response.GpsCheckResponse;
import SmartFoodStreet_Backend.entity.Stall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StallMapper.class})
public interface GpsMapper {

    @Mapping(target = "triggeredStall", source = "stall")
    GpsCheckResponse toResponse(Stall stall);

    default GpsCheckResponse empty() {
        return GpsCheckResponse.builder().build();
    }
}