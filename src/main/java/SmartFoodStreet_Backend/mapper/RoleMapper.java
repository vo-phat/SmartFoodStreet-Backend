package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.role.request.RoleRequest;
import SmartFoodStreet_Backend.dto.role.response.RoleResponse;
import SmartFoodStreet_Backend.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);

}
