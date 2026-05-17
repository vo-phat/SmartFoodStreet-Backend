package backend.service;

import backend.common.exception.AppException;
import backend.common.exception.ErrorCode;
import backend.dto.permission.request.PermissionCreatedEvent;
import backend.entity.Permission;
import backend.entity.Role;
import backend.repository.PermissionRepository;
import backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionEventListener {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    @EventListener
    public void handlePermissionCreated(PermissionCreatedEvent event) {

        Permission permission = permissionRepository.findById(event.getPermissionId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        adminRole.getPermissions().add(permission);

        roleRepository.save(adminRole);
    }
}