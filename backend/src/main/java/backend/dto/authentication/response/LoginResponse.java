package backend.dto.authentication.response;

import backend.dto.account.response.AccountResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String token;
    boolean authenticated;
    AccountResponse account;
}