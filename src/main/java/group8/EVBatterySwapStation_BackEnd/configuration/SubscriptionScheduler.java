package group8.EVBatterySwapStation_BackEnd.configuration;

import group8.EVBatterySwapStation_BackEnd.service.DriverSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {
    private final DriverSubscriptionService service;

    @Scheduled(cron = "0 0 0 * * *") // Chạy vào lúc nửa đêm mỗi ngày
    public void checkExpiredSubscriptions() {
        service.checkAndExpireSubscriptions();
    }
}
