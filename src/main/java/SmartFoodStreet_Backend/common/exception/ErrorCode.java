package SmartFoodStreet_Backend.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    INVALID_KEY(1000, "Invalid enum key"),
    USER_EXISTS(1001, "User already exists"),
    USER_NOT_EXISTS(1002, "User not exists"),
    INVALID_USERNAME(1003, "Username cannot be empty"),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters"),
    INVALID_EMAIL(1005, "Invalid email"),
    INVALID_ROLE(1006, "Role cannot be empty"),
    INVALID_IS_ACTIVE(1007, "IsActive cannot be empty"),
    UNAUTHENTICATED(1008, "Unauthenticated"),



    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    ;

    int code;
    String message;
}
