package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.role.request.RoleRequest;
import SmartFoodStreet_Backend.dto.role.response.RoleResponse;
import SmartFoodStreet_Backend.entity.Permission;
import SmartFoodStreet_Backend.entity.Role;
import SmartFoodStreet_Backend.mapper.RoleMapper;
import SmartFoodStreet_Backend.repository.PermissionRepository;
import SmartFoodStreet_Backend.repository.RoleRepository;
import SmartFoodStreet_Backend.service.interfaces.IRole;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService implements IRole {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        Set<String> requestedPermissions = new HashSet<>(roleRequest.getPermissions());

        List<Permission> permissions =
                permissionRepository.findAllByNameIn(requestedPermissions);

        if (permissions.size() != requestedPermissions.size()) {

            Set<String> foundNames = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            requestedPermissions.removeAll(foundNames);

            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Role role = roleMapper.toRole(roleRequest);
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(long roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if ("ADMIN".equalsIgnoreCase(role.getName())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getName() != null &&
                !request.getName().equalsIgnoreCase(role.getName())) {

            if (roleRepository.existsByName(request.getName())) {
                throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
            }

            role.setName(request.getName().toUpperCase());
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription().trim());
        }

        if (request.getPermissions() != null) {

            Set<String> requestedPermissionNames =
                    request.getPermissions()
                            .stream()
                            .map(String::toUpperCase)
                            .collect(Collectors.toSet());

            List<Permission> foundPermissions =
                    permissionRepository.findAllByNameIn(requestedPermissionNames);

            if (foundPermissions.size() != requestedPermissionNames.size()) {

                Set<String> foundNames = foundPermissions.stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());

                requestedPermissionNames.removeAll(foundNames);

                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
            }

            Set<Permission> currentPermissions = role.getPermissions();
            Set<Permission> newPermissions = new HashSet<>(foundPermissions);

            Set<Permission> toAdd = new HashSet<>(newPermissions);
            toAdd.removeAll(currentPermissions);

            Set<Permission> toRemove = new HashSet<>(currentPermissions);
            toRemove.removeAll(newPermissions);

            currentPermissions.addAll(toAdd);
            currentPermissions.removeAll(toRemove);

            role.setPermissions(currentPermissions);
        }

        Role updatedRole = roleRepository.save(role);

        return roleMapper.toRoleResponse(updatedRole);
    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @Override
    public void deleteRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        roleRepository.deleteByName(role.getName());
    }
}
