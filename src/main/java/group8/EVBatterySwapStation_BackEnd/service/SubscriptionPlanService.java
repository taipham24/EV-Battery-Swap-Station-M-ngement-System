package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlan createSubscription(SubscriptionPlan subscriptionPlan);

    List<SubscriptionPlan> getAllPlansSortedByPrice();

    List<SubscriptionPlan> getAllPlans();

    SubscriptionPlan update(Long id, SubscriptionPlan updatedPlan);

    void deactivatePlan(Long planId);

    Page<DriverSubscription> getPlanSubscribers(Long planId, Pageable pageable);

    Map<String, Object> getPlanStatistics(Long planId);
}
