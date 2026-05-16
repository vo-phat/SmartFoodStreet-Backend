package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.permission.request.PermissionRequest;
import SmartFoodStreet_Backend.dto.permission.response.PermissionResponse;

import java.util.List;

public interface IPermission {
    PermissionResponse createPermission(PermissionRequest permissionRequest);

    List<PermissionResponse> getAll();

    void deletePermission(String permission);
}
