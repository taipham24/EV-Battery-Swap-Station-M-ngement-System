package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SubscriptionPlanRequest;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;

public interface SubscriptionPlanService {
    SubscriptionPlan createSubscription(SubscriptionPlanRequest request);
}
