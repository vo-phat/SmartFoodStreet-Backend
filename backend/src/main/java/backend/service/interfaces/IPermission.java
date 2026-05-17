package backend.service.interfaces;

import backend.dto.permission.request.PermissionRequest;
import backend.dto.permission.response.PermissionResponse;

import java.util.List;

public interface IPermission {
    PermissionResponse createPermission(PermissionRequest permissionRequest);

    List<PermissionResponse> getAll();

    void deletePermission(String permission);
}