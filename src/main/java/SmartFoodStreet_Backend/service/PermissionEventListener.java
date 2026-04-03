package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.permission.request.PermissionCreatedEvent;
import SmartFoodStreet_Backend.entity.Permission;
import SmartFoodStreet_Backend.entity.Role;
import SmartFoodStreet_Backend.repository.PermissionRepository;
import SmartFoodStreet_Backend.repository.RoleRepository;
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