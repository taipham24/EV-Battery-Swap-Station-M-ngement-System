package group8.EVBatterySwapStation_BackEnd.configuration;

import group8.EVBatterySwapStation_BackEnd.service.StaffManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlaCheckScheduler {
    private final StaffManagementService service;

    // Chạy mỗi 1 tiếng
    @Scheduled(fixedRate = 3600000)
    public void runSlaCheck() {
        service.checkSlaAndEscalate();
    }
}
