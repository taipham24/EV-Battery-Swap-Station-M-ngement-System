package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SubscriptionPlanRequest;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import group8.EVBatterySwapStation_BackEnd.repository.SubscriptionPlanRepository;
import group8.EVBatterySwapStation_BackEnd.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanImpl implements SubscriptionPlanService {
    @Autowired
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionPlan createSubscription(SubscriptionPlanRequest request) {
        SubscriptionPlan subscriptionPlan = SubscriptionPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .swapLimit(request.getSwapLimit())
                .build();
        return subscriptionPlanRepository.save(subscriptionPlan);
    }
}
