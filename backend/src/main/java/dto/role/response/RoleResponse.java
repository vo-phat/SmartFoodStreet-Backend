package SmartFoodStreet_Backend.dto.role.response;

import SmartFoodStreet_Backend.dto.permission.response.PermissionResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String name;

    String description;

    Set<PermissionResponse> permissions;
}
