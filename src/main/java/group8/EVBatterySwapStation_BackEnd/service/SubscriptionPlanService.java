package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlan createSubscription(SubscriptionPlan subscriptionPlan);

    List<SubscriptionPlan> getAllPlansSortedByPrice();

    SubscriptionPlan update(Long id, SubscriptionPlan updatedPlan);
}
