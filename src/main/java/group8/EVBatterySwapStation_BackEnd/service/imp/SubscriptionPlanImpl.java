package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverSubscriptionRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SubscriptionPlanRepository;
import group8.EVBatterySwapStation_BackEnd.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionPlanImpl implements SubscriptionPlanService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final DriverSubscriptionRepository driverSubscriptionRepository;

    @Override
    public SubscriptionPlan createSubscription(SubscriptionPlan subscriptionPlan) {
        log.info("Creating subscription plan: {}", subscriptionPlan.getName());
        subscriptionPlan.setCreatedAt(LocalDateTime.now());
        subscriptionPlan.setUpdatedAt(LocalDateTime.now());
        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    @Override
    public List<SubscriptionPlan> getAllPlansSortedByPrice() {
        log.info("Getting all active subscription plans sorted by price");
        return subscriptionPlanRepository.findAllByActiveTrueOrderByPriceAsc();
    }

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        log.info("Getting all subscription plans");
        return subscriptionPlanRepository.findAll();
    }

    @Override
    @Transactional
    public SubscriptionPlan update(Long id, SubscriptionPlan updatedPlan) {
        log.info("Updating subscription plan {}", id);
        
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        
        existing.setName(updatedPlan.getName());
        existing.setDescription(updatedPlan.getDescription());
        existing.setPrice(updatedPlan.getPrice());
        existing.setDurationDays(updatedPlan.getDurationDays());
        existing.setSwapLimit(updatedPlan.getSwapLimit());
        existing.setPricePerSwap(updatedPlan.getPricePerSwap());
        existing.setPricePerExtraSwap(updatedPlan.getPricePerExtraSwap());
        existing.setUpdatedAt(LocalDateTime.now());
        
        return subscriptionPlanRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivatePlan(Long planId) {
        log.info("Deactivating subscription plan {}", planId);
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        
        plan.setActive(false);
        plan.setUpdatedAt(LocalDateTime.now());
        
        subscriptionPlanRepository.save(plan);
        log.info("Subscription plan {} deactivated successfully", planId);
    }

    @Override
    public Page<DriverSubscription> getPlanSubscribers(Long planId, Pageable pageable) {
        log.info("Getting subscribers for plan {}", planId);
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        
        return driverSubscriptionRepository.findByPlan(plan, pageable);
    }

    @Override
    public Map<String, Object> getPlanStatistics(Long planId) {
        log.info("Getting statistics for plan {}", planId);
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        
        List<DriverSubscription> subscriptions = driverSubscriptionRepository.findByPlan(plan, Pageable.unpaged())
                .getContent();
        
        long totalSubscriptions = subscriptions.size();
        long activeSubscriptions = subscriptions.stream()
                .mapToLong(sub -> sub.isActive() ? 1 : 0)
                .sum();
        
        long totalSwapsUsed = subscriptions.stream()
                .mapToLong(DriverSubscription::getSwapsUsed)
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("planId", planId);
        stats.put("planName", plan.getName());
        stats.put("totalSubscriptions", totalSubscriptions);
        stats.put("activeSubscriptions", activeSubscriptions);
        stats.put("totalSwapsUsed", totalSwapsUsed);
        stats.put("averageSwapsPerSubscription", totalSubscriptions > 0 ? totalSwapsUsed / (double) totalSubscriptions : 0.0);
        stats.put("createdAt", plan.getCreatedAt());
        stats.put("updatedAt", plan.getUpdatedAt());
        stats.put("active", plan.isActive());
        
        return stats;
    }
}
