package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import group8.EVBatterySwapStation_BackEnd.repository.SubscriptionPlanRepository;
import group8.EVBatterySwapStation_BackEnd.service.SubscriptionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanImpl implements SubscriptionPlanService {
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionPlan createSubscription(SubscriptionPlan subscriptionPlan) {
        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    @Override
    public List<SubscriptionPlan> getAllPlansSortedByPrice() {
        return subscriptionPlanRepository.findAllByOrderByPriceAsc();
    }
    @Override
    public SubscriptionPlan update(Long id, SubscriptionPlan updatedPlan) {
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription plan not found with ID: " + id));
        existing.setName(updatedPlan.getName());
        existing.setDescription(updatedPlan.getDescription());
        existing.setPrice(updatedPlan.getPrice());
        existing.setDurationDays(updatedPlan.getDurationDays());
        existing.setSwapLimit(updatedPlan.getSwapLimit());
        return subscriptionPlanRepository.save(existing);
    }
}
