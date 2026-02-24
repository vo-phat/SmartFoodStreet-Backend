package SmartFoodStreet_Backend.dto.account.response;

import SmartFoodStreet_Backend.dto.role.response.RoleResponse;
import SmartFoodStreet_Backend.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
public class AccountResponse {
    String username;

    String fullName;

    String email;

    Boolean isActive;

    Timestamp createdAt;

    Set<RoleResponse> roles;
}