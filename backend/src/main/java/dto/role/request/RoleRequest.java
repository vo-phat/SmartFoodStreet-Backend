package SmartFoodStreet_Backend.dto.role.request;

import SmartFoodStreet_Backend.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @NotBlank(message = "INVALID_ROLE")
    String name;

    String description;

    Set<String> permissions;
}
