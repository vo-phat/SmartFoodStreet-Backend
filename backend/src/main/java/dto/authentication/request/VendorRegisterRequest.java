package SmartFoodStreet_Backend.dto.authentication.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VendorRegisterRequest {
    @NotBlank
    String ownerName;

    @NotBlank
    String stallName;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String password;
}
