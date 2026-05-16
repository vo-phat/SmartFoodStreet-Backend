package SmartFoodStreet_Backend.configuration;

import SmartFoodStreet_Backend.dto.authentication.request.IntrospectRequest;
import SmartFoodStreet_Backend.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder;

    @PostConstruct
    public void init() {
        byte[] secretKey = Base64.getDecoder().decode(jwtSecret);

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(secretKey, "HmacSHA256");

        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {

            var response = authenticationService.introspect(
                    IntrospectRequest.builder()
                            .token(token)
                            .build()
            );

            if (!response.isValid()) {
                throw new JwtException("Token invalid or revoked");
            }

            return nimbusJwtDecoder.decode(token);

        } catch (Exception ex) {
            throw new JwtException("Authentication failed", ex);
        }
    }
}