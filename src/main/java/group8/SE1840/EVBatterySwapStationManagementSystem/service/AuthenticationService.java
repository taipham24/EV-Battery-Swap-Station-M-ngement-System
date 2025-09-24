package group8.SE1840.EVBatterySwapStationManagementSystem.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.AuthenticationRequest;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.VerifyTokenRequest;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.AuthenticationResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.TokenResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.VerifyTokenResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Driver;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.TrackingUserLogin;
import group8.SE1840.EVBatterySwapStationManagementSystem.exception.AppException;
import group8.SE1840.EVBatterySwapStationManagementSystem.exception.ErrorCode;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.DriverRepository;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.InvalidatedTokenRepository;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.TrackingUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    final DriverRepository driverRepository;
    final InvalidatedTokenRepository invalidatedTokenRepository;
    final TrackingUserRepository trackingUserRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNAL_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var driver = driverRepository.findByUserName(authenticationRequest.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        boolean status = driver.isStatus();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), driver.getPassword());
        if (!authenticated || !status) throw new AppException(ErrorCode.UNAUTHENDICATED);
        TokenResponse accessToken = generateToken(driver);
        TokenResponse refreshToken = generateRefreshToken(driver);
        AuthenticationResponse authRes = new AuthenticationResponse();
        authRes.setToken(accessToken.getToken());
        authRes.setRefreshToken(refreshToken.getToken());
        trackingLogin(driver.getDriverId(), accessToken.getExpirationTime());
        return authRes;
    }

    public TokenResponse generateToken(Driver driver) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(driver.getDriverId().toString())
                .issuer("EVBatterySwapStationManagementSystem.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        //Set time for token
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli() // expired in 1 hour
                ))
                .jwtID(UUID.randomUUID().toString()) // token ID
                .claim("scope", buildScope(driver)) // info of token claim
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNAL_KEY.getBytes()));
            return new TokenResponse(jwsObject.serialize(), jwtClaimsSet.getExpirationTime());
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private TokenResponse generateRefreshToken(Driver driver) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(driver.getDriverId()))
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli())) // 7 ngày
                .jwtID(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .build();
        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNAL_KEY.getBytes()));
            return new TokenResponse(jwsObject.serialize(), claims.getExpirationTime());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public VerifyTokenResponse verifyToken(VerifyTokenRequest request) {
        var token = request.getToken();
        VerifyTokenResponse response = new VerifyTokenResponse();
        boolean isValid = true;
        try {
            tokenCheck(token);
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        } catch (AppException e) {
            isValid = false;
        }
        response.setValid(isValid);
        return response;
    }

    private SignedJWT tokenCheck(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNAL_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!verified || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENDICATED);
        }
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENDICATED);
        }
        return signedJWT;
    }


    public String buildScope(Driver driver) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(driver.getRoles())) {
            driver.getRoles().forEach(roles -> {
                stringJoiner.add(roles.getRoleType());
            });

        }
        return stringJoiner.toString();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void trackingLogin(Long driverId, Date expiredTime) {
        TrackingUserLogin trackingUserLogin = new TrackingUserLogin();
        trackingUserLogin.setDriverId(driverId);
        trackingUserLogin.setLoginTime(new Date());
        trackingUserLogin.setExpiredTime(expiredTime);
        trackingUserRepository.save(trackingUserLogin);
    }
}
