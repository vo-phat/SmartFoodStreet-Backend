package backend.dto.account.response;

import backend.dto.role.response.RoleResponse;
import backend.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
public class AccountResponse {
    Long id;

    String userName;

    String fullName;

    String email;

    Boolean isActive;

    Timestamp createdAt;

    Set<RoleResponse> roles;
}