package backend.mapper;

import backend.dto.role.request.RoleRequest;
import backend.dto.role.response.RoleResponse;
import backend.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);

}