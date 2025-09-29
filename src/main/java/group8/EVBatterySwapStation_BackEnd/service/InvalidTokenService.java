package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.repository.InvalidatedTokenRepository;
import group8.EVBatterySwapStation_BackEnd.repository.TrackingUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvalidTokenService {
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;
    @Autowired
    private TrackingUserRepository trackingUserRepository;

    public void deleteExpiredTime() {
        java.util.Date currentTime = new java.util.Date();
        invalidatedTokenRepository.deleteExpiredTokens(currentTime);
    }
}
