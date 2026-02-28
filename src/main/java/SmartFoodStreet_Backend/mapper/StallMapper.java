package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.stall.request.StallCreationRequest;
import SmartFoodStreet_Backend.dto.stall.request.StallUpdateRequest;
import SmartFoodStreet_Backend.dto.stall.response.StallResponse;
import SmartFoodStreet_Backend.entity.Stall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StallMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "street", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "recommendationScore", constant = "0")
    @Mapping(target = "createdAt", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "updatedAt", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    Stall toEntity(StallCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "street", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(@MappingTarget Stall entity, StallUpdateRequest request);

    @Mapping(target = "streetId", source = "street.id")
    @Mapping(target = "vendorId", source = "vendor.id")
    StallResponse toResponse(Stall entity);
}