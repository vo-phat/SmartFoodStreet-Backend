package SmartFoodStreet_Backend.dto.account.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountUpdateRequest {
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    String fullName;

    @Email(message = "INVALID_EMAIL")
    String email;

    List<String> roles;

    @NotNull(message = "INVALID_IS_ACTIVE")
    Boolean isActive;
}
