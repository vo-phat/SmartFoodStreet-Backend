package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.authentication.request.*;
import SmartFoodStreet_Backend.dto.authentication.response.IntrospectResponse;
import SmartFoodStreet_Backend.dto.authentication.response.LoginResponse;
import SmartFoodStreet_Backend.dto.authentication.response.RegisterResponse;
import SmartFoodStreet_Backend.entity.Account;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface IAuthentication {
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;

    RegisterResponse register(RegisterRequest registerRequest);

    RegisterResponse registerVendor(VendorRegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse loginByEmail(VendorLoginRequest loginRequest);

    void logout(LogoutRequest token);

    SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException;

    String generateToken(Account account);

    LoginResponse refreshToken(RefreshRequest requestRequest) throws ParseException, JOSEException;

}
