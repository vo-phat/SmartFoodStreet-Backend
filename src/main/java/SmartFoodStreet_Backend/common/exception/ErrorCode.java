package SmartFoodStreet_Backend.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // ================= VALIDATION ERROR (1000 - 1999) =================
    INVALID_KEY(1000, "Invalid enum key", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1001, "Username cannot be empty", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1002, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1003, "Invalid email", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1004, "Role cannot be empty", HttpStatus.BAD_REQUEST),
    INVALID_IS_ACTIVE(1005, "IsActive cannot be empty", HttpStatus.BAD_REQUEST),
    INVALID_DATE(1006, "Invalid date", HttpStatus.BAD_REQUEST),
    INVALID_PERMISSION(1007, "Invalid permission", HttpStatus.BAD_REQUEST),

    // ================= BUSINESS ERROR (2000 - 2999) =================
    USER_ALREADY_EXISTS(2000, "User already exists", HttpStatus.CONFLICT),
    USER_NOT_EXISTS(2001, "User not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(2002, "Role not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(2003, "Resource not found", HttpStatus.NOT_FOUND),
    PERMISSION_ALREADY_EXISTS(2004, "Permission already exists", HttpStatus.CONFLICT),
    ROLE_ALREADY_EXISTS(2005, "Role already exists", HttpStatus.CONFLICT),

    // ================= AUTHENTICATION / AUTHORIZATION (3000 - 3999) =================
    UNAUTHENTICATED(3000, "Authentication required. Please provide a valid access token.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(3001, "The provided access token is invalid, expired, or cannot be verified.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(3002, "Access denied. You do not have sufficient permissions to perform this action.", HttpStatus.FORBIDDEN),

    // ================= SYSTEM ERROR (5000 - 5999) =================
    FILE_UPLOAD_FAILED(5000, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED(5001, "File delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION(9999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
