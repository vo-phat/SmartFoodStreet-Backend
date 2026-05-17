package backend.controller;

import backend.common.response.ApiResponse;
import backend.dto.role.request.RoleRequest;
import backend.dto.role.response.RoleResponse;
import backend.service.RoleService;
import com.cloudinary.Api;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping()
    public ApiResponse<RoleResponse> createRole (@RequestBody @Valid RoleRequest roleRequest){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @PutMapping("/{roleId}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable long roleId, @RequestBody @Valid RoleRequest roleRequest){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(roleId, roleRequest))
                .build();
    }

    @GetMapping()
    public ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{roleName}")
    public ApiResponse<Void> deleteRole (@PathVariable String roleName){
        roleService.deleteRole(roleName);

        return ApiResponse.<Void>builder()
                .message("Delete Successfully")
                .build();
    }
}