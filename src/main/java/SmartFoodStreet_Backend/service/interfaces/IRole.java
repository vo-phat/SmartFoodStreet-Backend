package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.role.request.RoleRequest;
import SmartFoodStreet_Backend.dto.role.response.RoleResponse;

import java.util.List;

public interface IRole {
    RoleResponse createRole(RoleRequest roleRequest);

    RoleResponse updateRole(Integer roleId, RoleRequest roleRequest);

    List<RoleResponse> getAll();

    void deleteRole(String roleName);
}
