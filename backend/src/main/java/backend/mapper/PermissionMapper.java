package backend.mapper;

import backend.dto.permission.request.PermissionRequest;
import backend.dto.permission.response.PermissionResponse;
import backend.entity.Permission;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePermission(@MappingTarget Permission permission, PermissionRequest permissionRequest);

    PermissionResponse toPermissionResponse(Permission permission);
}