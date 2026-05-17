package backend.service.interfaces;

import backend.dto.authentication.request.*;
import backend.dto.authentication.response.IntrospectResponse;
import backend.dto.authentication.response.LoginResponse;
import backend.dto.authentication.response.RegisterResponse;
import backend.entity.Account;
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