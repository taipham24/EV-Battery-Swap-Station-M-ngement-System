package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverSubscriptionRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverSubscriptionRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SubscriptionPlanRepository;
import group8.EVBatterySwapStation_BackEnd.service.DriverSubscriptionService;
import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverSubscriptionImpl implements DriverSubscriptionService {
    @Autowired
    private DriverSubscriptionRepository repository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    @Autowired
    private BatteryRepository batteryRepository;

    @Override
    public DriverSubscription createSubscription(DriverSubscriptionRequest request) {
        Long driverId = SecurityUtils.currentUserId();
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        Battery battery = batteryRepository.findById(request.getBatteryId())
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_FOUND));
        Driver driver = new Driver();
        driver.setDriverId(driverId);
        DriverSubscription sub = new DriverSubscription();
        sub.setDriver(driver);
        sub.setPlan(plan);
        sub.setBattery(battery);
        sub.setStartDate(LocalDateTime.now());
        sub.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        sub.setSwapsUsed(0);
        sub.setActive(true);
        sub.setAutoRenew(request.isAutoRenew());
        return repository.save(sub);
    }

    @Override
    public DriverSubscription getActiveSubscriptionForDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        return repository.findByDriverAndActiveTrue(driver)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_INACTIVE));
    }

    public void checkAndExpireSubscriptions() {
        List<DriverSubscription> all = repository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (DriverSubscription sub : all) {
            if (sub.isActive() && now.isAfter(sub.getEndDate())) {
                sub.setActive(false);
                if (sub.isAutoRenew()) {
                    sub.setStartDate(now);
                    sub.setEndDate(now.plusDays(sub.getPlan().getDurationDays()));
                    sub.setSwapsUsed(0);
                    sub.setActive(true);
                }
                repository.save(sub);
            }
        }
    }

}
