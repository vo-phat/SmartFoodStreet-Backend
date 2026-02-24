package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.authentication.request.*;
import SmartFoodStreet_Backend.dto.authentication.response.IntrospectResponse;
import SmartFoodStreet_Backend.dto.authentication.response.LoginResponse;
import SmartFoodStreet_Backend.dto.authentication.response.RegisterResponse;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.entity.InvalidatedToken;
import SmartFoodStreet_Backend.entity.Role;
import SmartFoodStreet_Backend.repository.AccountRepository;
import SmartFoodStreet_Backend.repository.InvalidatedTokenRepository;
import SmartFoodStreet_Backend.repository.RoleRepository;
import SmartFoodStreet_Backend.service.interfaces.IAuthentication;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationService implements IAuthentication {
    AccountRepository accountRepository;
    RoleRepository roleRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.secret}")
    @NonFinal
    String SINGER_KEY;

    @Value("${jwt.valid-duration}")
    @NonFinal
    long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    @NonFinal
    long REFRESHABLE_DURATION;

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            verifyToken(request.getToken(), false);

            return IntrospectResponse.builder()
                    .valid(true)
                    .build();

        } catch (Exception e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (accountRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String passwordHash = passwordEncoder.encode(registerRequest.getPassword());

        Role staffRole = roleRepository.findByName("STAFF")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("STAFF")
                            .build();
                    return roleRepository.save(role);
                });

        Account account = Account.builder()
                .userName(registerRequest.getUserName())
                .password(passwordHash)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .roles(Set.of(staffRole))
                .isActive(true)
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

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(account);

        return LoginResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(new java.sql.Date(expiryTime.getTime()))
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException | ParseException | JOSEException exception) {
            log.info("Logout already expired", exception);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(
                Base64.getDecoder().decode(SINGER_KEY)
        );

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if(invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }


    @Override
    public String generateToken(Account account) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        List<String> roles = account.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getUserName())
                .claim("scope", buildScope(account))
                .issuer("vn.edu.sgu.smartfoodstreet")
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS)
                ))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            byte[] secretKey = Base64.getDecoder().decode(SINGER_KEY);
            jwsObject.sign(new MACSigner(secretKey));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public LoginResponse refreshToken (RefreshRequest refreshRequest) throws ParseException, JOSEException {
        var signJwt = verifyToken(refreshRequest.getToken(), true);

        String jit = signJwt.getJWTClaimsSet().getJWTID();
        Date expiryTime = signJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(new java.sql.Date(expiryTime.getTime()))
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var userName = signJwt.getJWTClaimsSet().getSubject();

        var account = accountRepository.findByUserName(userName).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );

        var token = generateToken(account);

        return LoginResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if(!CollectionUtils.isEmpty(account.getRoles())) {
            account.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_"+role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }

        return stringJoiner.toString();
    }
}
