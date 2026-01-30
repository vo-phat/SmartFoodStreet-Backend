package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.authentication.request.LoginRequest;
import SmartFoodStreet_Backend.dto.authentication.request.RegisterRequest;
import SmartFoodStreet_Backend.dto.authentication.response.LoginResponse;
import SmartFoodStreet_Backend.dto.authentication.response.RegisterResponse;

public interface IAuthentication {
    RegisterResponse register (RegisterRequest registerRequest);

    LoginResponse login (LoginRequest loginRequest);

    String generateToken (String userName);
}
