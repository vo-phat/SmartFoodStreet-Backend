package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.street.request.FoodStreetCreationRequest;
import SmartFoodStreet_Backend.dto.street.request.FoodStreetUpdateRequest;
import SmartFoodStreet_Backend.dto.street.response.FoodStreetResponse;
import SmartFoodStreet_Backend.entity.FoodStreet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FoodStreetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "updatedAt", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    FoodStreet toEntity(FoodStreetCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(@MappingTarget FoodStreet entity, FoodStreetUpdateRequest request);

    FoodStreetResponse toResponse(FoodStreet entity);
}