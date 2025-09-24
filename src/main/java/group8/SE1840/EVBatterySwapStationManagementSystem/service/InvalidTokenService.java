package group8.SE1840.EVBatterySwapStationManagementSystem.service;

import group8.SE1840.EVBatterySwapStationManagementSystem.repository.InvalidatedTokenRepository;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.TrackingUserRepository;
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
