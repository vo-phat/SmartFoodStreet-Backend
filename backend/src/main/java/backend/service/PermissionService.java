package backend.service;

import backend.common.exception.AppException;
import backend.common.exception.ErrorCode;
import backend.dto.permission.request.PermissionCreatedEvent;
import backend.dto.permission.request.PermissionRequest;
import backend.dto.permission.response.PermissionResponse;
import backend.entity.Permission;
import backend.mapper.PermissionMapper;
import backend.repository.PermissionRepository;
import backend.service.interfaces.IPermission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void deletePermission (String permissionName) {
        Permission permission = permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        permissionRepository.deleteByName(permission.getName());
    }
}