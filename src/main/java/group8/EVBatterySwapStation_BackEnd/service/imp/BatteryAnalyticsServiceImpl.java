package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryHealthMetrics;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryLedgerAction;
import group8.EVBatterySwapStation_BackEnd.enums.InspectionCondition;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.*;
import group8.EVBatterySwapStation_BackEnd.service.BatteryAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatteryAnalyticsServiceImpl implements BatteryAnalyticsService {
    
    private final BatteryRepository batteryRepository;
    private final BatteryLedgerRepository batteryLedgerRepository;
    private final BatteryInspectionRepository batteryInspectionRepository;

    @Override
    public BatteryHealthMetrics calculateBatteryHealth(Long batteryId) {
        Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));

        // Get usage data
        long totalSwapCount = batteryLedgerRepository.countByBatteryAndAction(battery, BatteryLedgerAction.DISPENSE);
        
        // Get inspection history
        List<BatteryInspection> inspections = batteryInspectionRepository.findByBatteryOrderByInspectedAtDesc(battery, Pageable.unpaged()).getContent();
        
        // Calculate SoH using the algorithm
        double soh = calculateStateOfHealth(battery, inspections, totalSwapCount);
        
        // Calculate average SoC on return
        double averageSocOnReturn = inspections.stream()
                .mapToInt(BatteryInspection::getSocPercent)
                .average()
                .orElse(0.0);
        
        // Calculate degradation rate (simplified)
        double degradationRate = calculateDegradationRate(inspections);
        
        // Get current condition
        InspectionCondition currentCondition = inspections.isEmpty() ? 
                InspectionCondition.GOOD : inspections.get(0).getCondition();
        
        // Build usage timeline
        List<BatteryLedger> ledgerEntries = batteryLedgerRepository.findByBatteryOrderByCreatedAtDesc(battery, Pageable.unpaged()).getContent();
        List<BatteryHealthMetrics.UsageEvent> usageTimeline = ledgerEntries.stream()
                .map(entry -> BatteryHealthMetrics.UsageEvent.builder()
                        .timestamp(entry.getCreatedAt())
                        .action(entry.getAction().name())
                        .stationName(entry.getStation().getName())
                        .notes("Status: " + entry.getNewStatus())
                        .build())
                .collect(Collectors.toList());
        
        // Build inspection history
        List<BatteryHealthMetrics.InspectionSummary> inspectionHistory = inspections.stream()
                .map(inspection -> BatteryHealthMetrics.InspectionSummary.builder()
                        .inspectedAt(inspection.getInspectedAt())
                        .condition(inspection.getCondition())
                        .socPercent(inspection.getSocPercent())
                        .notes(inspection.getNotes())
                        .inspectorName(inspection.getInspector() != null ? 
                                inspection.getInspector().getDriver().getFullName() : "Unknown")
                        .build())
                .collect(Collectors.toList());

        return BatteryHealthMetrics.builder()
                .batteryId(battery.getBatteryId())
                .serialNumber(battery.getSerialNumber())
                .model(battery.getModel())
                .capacityWh(battery.getCapacityWh())
                .stateOfHealthPercent(soh)
                .totalSwapCount(totalSwapCount)
                .averageSocOnReturn(averageSocOnReturn)
                .degradationRate(degradationRate)
                .currentCondition(currentCondition)
                .usageTimeline(usageTimeline)
                .inspectionHistory(inspectionHistory)
                .build();
    }

    @Override
    public Page<BatteryHealthMetrics.UsageEvent> getBatteryUsageHistory(Long batteryId, Pageable pageable) {
        Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
        
        Page<BatteryLedger> ledgerPage = batteryLedgerRepository.findByBatteryOrderByCreatedAtDesc(battery, pageable);
        
        return ledgerPage.map(entry -> BatteryHealthMetrics.UsageEvent.builder()
                .timestamp(entry.getCreatedAt())
                .action(entry.getAction().name())
                .stationName(entry.getStation().getName())
                .notes("Status: " + entry.getNewStatus())
                .build());
    }

    @Override
    public Page<BatteryHealthMetrics.InspectionSummary> getBatteryInspectionHistory(Long batteryId, Pageable pageable) {
        Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
        
        Page<BatteryInspection> inspectionPage = batteryInspectionRepository.findByBatteryOrderByInspectedAtDesc(battery, pageable);
        
        return inspectionPage.map(inspection -> BatteryHealthMetrics.InspectionSummary.builder()
                .inspectedAt(inspection.getInspectedAt())
                .condition(inspection.getCondition())
                .socPercent(inspection.getSocPercent())
                .notes(inspection.getNotes())
                .inspectorName(inspection.getInspector() != null ? 
                        inspection.getInspector().getDriver().getFullName() : "Unknown")
                .build());
    }

    @Override
    public Map<String, Object> getSystemWideBatteryHealthReport() {
        List<Battery> allBatteries = batteryRepository.findAll();
        
        Map<InspectionCondition, Long> conditionBreakdown = allBatteries.stream()
                .map(battery -> {
                    Optional<BatteryInspection> latestInspection = 
                            batteryInspectionRepository.findFirstByBatteryOrderByInspectedAtDesc(battery);
                    return latestInspection.map(BatteryInspection::getCondition).orElse(InspectionCondition.GOOD);
                })
                .collect(Collectors.groupingBy(condition -> condition, Collectors.counting()));
        
        // Calculate average SoH across all batteries
        double averageSoH = allBatteries.stream()
                .mapToDouble(battery -> {
                    List<BatteryInspection> inspections = batteryInspectionRepository
                            .findByBatteryOrderByInspectedAtDesc(battery, Pageable.unpaged()).getContent();
                    long swapCount = batteryLedgerRepository.countByBatteryAndAction(battery, BatteryLedgerAction.DISPENSE);
                    return calculateStateOfHealth(battery, inspections, swapCount);
                })
                .average()
                .orElse(100.0);
        
        // Count batteries needing attention
        long batteriesNeedingAttention = allBatteries.stream()
                .mapToLong(battery -> {
                    Optional<BatteryInspection> latestInspection = 
                            batteryInspectionRepository.findFirstByBatteryOrderByInspectedAtDesc(battery);
                    if (latestInspection.isPresent()) {
                        InspectionCondition condition = latestInspection.get().getCondition();
                        return (condition == InspectionCondition.DAMAGED || condition == InspectionCondition.DEGRADED) ? 1 : 0;
                    }
                    return 0;
                })
                .sum();
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalBatteries", allBatteries.size());
        report.put("averageSoH", Math.round(averageSoH * 100.0) / 100.0);
        report.put("conditionBreakdown", conditionBreakdown);
        report.put("batteriesNeedingAttention", batteriesNeedingAttention);
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    /**
     * Calculate State of Health using the algorithm:
     * SoH = 100 - (cyclesPenalty + conditionPenalty + socPenalty)
     */
    private double calculateStateOfHealth(Battery battery, List<BatteryInspection> inspections, long swapCount) {
        double cyclesPenalty = Math.min(swapCount * 0.1, 30); // Max 30% penalty for cycles
        
        double conditionPenalty = 0;
        if (!inspections.isEmpty()) {
            InspectionCondition latestCondition = inspections.get(0).getCondition();
            switch (latestCondition) {
                case DAMAGED:
                    conditionPenalty = 40;
                    break;
                case DEGRADED:
                    conditionPenalty = 20;
                    break;
                case GOOD:
                default:
                    conditionPenalty = 0;
                    break;
            }
        }
        
        double socPenalty = 0;
        if (!inspections.isEmpty()) {
            double averageSoc = inspections.stream()
                    .mapToInt(BatteryInspection::getSocPercent)
                    .average()
                    .orElse(100.0);
            socPenalty = Math.max(0, (100 - averageSoc) * 0.2); // Penalty for low SoC
        }
        
        double soh = 100 - (cyclesPenalty + conditionPenalty + socPenalty);
        return Math.max(0, Math.min(100, soh)); // Clamp between 0-100
    }

    /**
     * Calculate degradation rate based on inspection trends
     */
    private double calculateDegradationRate(List<BatteryInspection> inspections) {
        if (inspections.size() < 2) {
            return 0.0;
        }
        
        // Simple linear regression on SoC over time
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                inspections.get(inspections.size() - 1).getInspectedAt(),
                inspections.get(0).getInspectedAt()
        );
        
        if (daysBetween == 0) {
            return 0.0;
        }
        
        int socDifference = inspections.get(0).getSocPercent() - 
                inspections.get(inspections.size() - 1).getSocPercent();
        
        return (double) socDifference / daysBetween; // SoC change per day
    }
}
