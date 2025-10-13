package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryHealthMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BatteryAnalyticsService {
    BatteryHealthMetrics calculateBatteryHealth(Long batteryId);
    
    Page<BatteryHealthMetrics.UsageEvent> getBatteryUsageHistory(Long batteryId, Pageable pageable);
    
    Page<BatteryHealthMetrics.InspectionSummary> getBatteryInspectionHistory(Long batteryId, Pageable pageable);
    
    Map<String, Object> getSystemWideBatteryHealthReport();
}
