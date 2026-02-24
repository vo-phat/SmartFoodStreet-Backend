package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.permission.request.PermissionCreatedEvent;
import SmartFoodStreet_Backend.dto.permission.request.PermissionRequest;
import SmartFoodStreet_Backend.dto.permission.response.PermissionResponse;
import SmartFoodStreet_Backend.entity.Permission;
import SmartFoodStreet_Backend.mapper.PermissionMapper;
import SmartFoodStreet_Backend.repository.PermissionRepository;
import SmartFoodStreet_Backend.service.interfaces.IPermission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService implements IPermission {
    PermissionRepository permissionRepository;
    ApplicationEventPublisher eventPublisher;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse createPermission(PermissionRequest permissionRequest) {

        if (permissionRepository.existsByName(permissionRequest.getName())) {
            throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }

        Permission permission = permissionMapper.toPermission(permissionRequest);

        permission = permissionRepository.save(permission);

        eventPublisher.publishEvent(
                new PermissionCreatedEvent(permission.getId())
        );

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAll () {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    public void deletePermission (String permissionName) {
        Permission permission = permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        permissionRepository.deleteByName(permission.getName());
    }
}
