package SmartFoodStreet_Backend.dto.account.request;

import SmartFoodStreet_Backend.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountUpdateRequest {
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    String fullName;

    @Email(message = "INVALID_EMAIL")
    String email;

    @NotBlank(message = "INVALID_ROLE")
    Account.Role role;

    @NotBlank(message = "INVALID_IS_ACTIVE")
    Boolean isActive;
}
