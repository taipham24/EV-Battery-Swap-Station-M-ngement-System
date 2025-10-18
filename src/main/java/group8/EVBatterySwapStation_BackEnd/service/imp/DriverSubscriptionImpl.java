package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverSubscriptionRequest;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentStatus;
import group8.EVBatterySwapStation_BackEnd.enums.SubscriptionStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.*;
import group8.EVBatterySwapStation_BackEnd.service.DriverSubscriptionService;
import group8.EVBatterySwapStation_BackEnd.service.EmailService;
import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import jakarta.transaction.Transactional;
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
    @Autowired
    private DriverSubscriptionRepository driverSubscriptionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public DriverSubscription createSubscription(DriverSubscriptionRequest request) {
        Long driverId = SecurityUtils.currentUserId();
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        Driver driver = new Driver();
        driver.setDriverId(driverId);
        DriverSubscription sub = new DriverSubscription();
        sub.setDriver(driver);
        sub.setPlan(plan);
        sub.setStartDate(LocalDateTime.now());
        sub.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        sub.setSwapsUsed(0);
        sub.setActive(false); // Will be activated after payment
        sub.setStatus(SubscriptionStatus.PENDING_PAYMENT);
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

    @Override
    public List<DriverSubscription> getAllSubscriptionsForDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        return repository.findByDriver(driver);
    }

    @Override
    public DriverSubscription cancelSubscription(Long subscriptionId) {
        DriverSubscription sub = repository.findById(subscriptionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        if (!sub.isActive()) {
            throw new AppException(ErrorCode.SUBSCRIPTION_INACTIVE);
        }
        sub.setActive(false);
        return repository.save(sub);
    }

    @Override
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

    @Override
    @Transactional
    public void renewSubscription(Long subscriptionId) {
        DriverSubscription oldSub = repository.findById(subscriptionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        oldSub.setActive(false);
        oldSub.setStatus(SubscriptionStatus.EXPIRED);
        repository.save(oldSub);

        DriverSubscription newSub = new DriverSubscription();
        newSub.setDriver(oldSub.getDriver());
        newSub.setPlan(oldSub.getPlan());
        newSub.setStartDate(LocalDateTime.now());
        newSub.setEndDate(LocalDateTime.now().plusDays(oldSub.getPlan().getDurationDays()));
        newSub.setSwapsUsed(0);
        newSub.setActive(true);
        newSub.setAutoRenew(true);
        newSub.setStatus(SubscriptionStatus.ACTIVE);
        repository.save(newSub);

        Payment payment = new Payment();
        payment.setSubscription(newSub);
        payment.setMethod(PaymentMethod.CREDIT_CARD);
        payment.setAmountVnd(oldSub.getPlan().getPrice());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        emailService.sendRenewalSuccessEmail(oldSub.getDriver().getEmail(), newSub);
    }

}
