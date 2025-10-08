package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverSubscriptionRequest;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;

import java.util.List;

public interface DriverSubscriptionService {
    DriverSubscription createSubscription(DriverSubscriptionRequest request);

    DriverSubscription getActiveSubscriptionForDriver(Long driverId);

    List<DriverSubscription> getAllSubscriptionsForDriver(Long driverId);
}
