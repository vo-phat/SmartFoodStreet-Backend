package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.authentication.request.*;
import SmartFoodStreet_Backend.dto.authentication.response.IntrospectResponse;
import SmartFoodStreet_Backend.dto.authentication.response.LoginResponse;
import SmartFoodStreet_Backend.dto.authentication.response.RegisterResponse;
import SmartFoodStreet_Backend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {
    public final AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authenticationService.register(registerRequest);

        return ApiResponse.<RegisterResponse>builder()
                .result(registerResponse)
                .build();
    }

    @PostMapping("/register-vendor")
    ApiResponse<RegisterResponse> registerVendor(@RequestBody VendorRegisterRequest registerRequest) {
        RegisterResponse registerResponse = authenticationService.registerVendor(registerRequest);

        return ApiResponse.<RegisterResponse>builder()
                .result(registerResponse)
                .build();
    }

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);

        return ApiResponse.<LoginResponse>builder()
                .result(loginResponse)
                .build();
    }

    @PostMapping("/login-email")
    ApiResponse<LoginResponse> loginEmail(@RequestBody VendorLoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.loginByEmail(loginRequest);

        return ApiResponse.<LoginResponse>builder()
                .result(loginResponse)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);

        return ApiResponse.<Void>builder()
                .message("Logout successfully")
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<LoginResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<LoginResponse>builder()
                .result(result)
                .build();
    }
}