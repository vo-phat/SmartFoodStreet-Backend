package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.authentication.request.LoginRequest;
import SmartFoodStreet_Backend.dto.authentication.request.RegisterRequest;
import SmartFoodStreet_Backend.dto.authentication.response.LoginResponse;
import SmartFoodStreet_Backend.dto.authentication.response.RegisterResponse;
import SmartFoodStreet_Backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    public final AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<RegisterResponse> register (@RequestBody RegisterRequest registerRequest){
        RegisterResponse registerResponse = authenticationService.register(registerRequest);

        return ApiResponse.<RegisterResponse>builder()
                .result(registerResponse)
                .build();
    }

    @PostMapping("/login")
    ApiResponse<LoginResponse> login (@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authenticationService.login(loginRequest);

        return ApiResponse.<LoginResponse>builder()
                .result(loginResponse)
                .build();
    }
}
