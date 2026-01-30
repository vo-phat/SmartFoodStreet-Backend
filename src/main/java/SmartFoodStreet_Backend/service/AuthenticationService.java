package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.authentication.request.LoginRequest;
import SmartFoodStreet_Backend.dto.authentication.request.RegisterRequest;
import SmartFoodStreet_Backend.dto.authentication.response.LoginResponse;
import SmartFoodStreet_Backend.dto.authentication.response.RegisterResponse;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.repository.AccountRepository;
import SmartFoodStreet_Backend.service.interfaces.IAuthentication;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class AuthenticationService implements IAuthentication {
    private final AccountRepository accountRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthenticationService (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public RegisterResponse register (RegisterRequest registerRequest) {
        if(accountRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String passwordHash = passwordEncoder.encode(registerRequest.getPassword());

        Account account = Account.builder()
                .userName(registerRequest.getUserName())
                .password(passwordHash)
                .build();

        Account result = accountRepository.save(account);

        return RegisterResponse.builder()
                .accountId(result.getId())
                .userName(result.getUserName())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        var account = accountRepository.findByUserName(loginRequest.getUserName())
                      .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(loginRequest.getPassword(), account.getPassword());

        if(!authenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(loginRequest.getUserName());

        return LoginResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public String generateToken(String userName) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userName)
                .claim("accountId", "Account ID Value")
                .issuer("vn.edu.sgu.smartfoodstreet")
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plus(1, ChronoUnit.HOURS)
                ))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            byte[] secretKey = Base64.getDecoder().decode(jwtSecret);
            jwsObject.sign(new MACSigner(secretKey));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

}
