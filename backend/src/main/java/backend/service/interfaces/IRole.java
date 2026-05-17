package backend.service.interfaces;

import backend.dto.role.request.RoleRequest;
import backend.dto.role.response.RoleResponse;

import java.util.List;

public interface IRole {
    RoleResponse createRole(RoleRequest roleRequest);

    RoleResponse updateRole(long roleId, RoleRequest roleRequest);

    List<RoleResponse> getAll();

    void deleteRole(String roleName);
}