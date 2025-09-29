package group8.EVBatterySwapStation_BackEnd.configuration;


import group8.EVBatterySwapStation_BackEnd.DTO.request.VerifyTokenRequest;
import group8.EVBatterySwapStation_BackEnd.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJWTDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String SIGNAL_KEY;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    public Jwt decode(String token) {
        // Check token valid

        VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest();
        verifyTokenRequest.setToken(token);
        var response = authenticationService.verifyToken(verifyTokenRequest);
        if (!response.isValid()) throw new JwtException("This token is invalid");

        if (Objects.isNull(nimbusJwtDecoder)) {

            // SecretKeySpec with 2 param: our signerKey from application.properties and algorithm that we are using to create token header
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNAL_KEY.getBytes(), "HS512");

            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
