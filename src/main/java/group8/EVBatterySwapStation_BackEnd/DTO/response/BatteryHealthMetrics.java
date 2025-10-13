package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.InspectionCondition;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryHealthMetrics {
    private Long batteryId;
    private String serialNumber;
    private String model;
    private Integer capacityWh;
    
    // Health metrics
    private Double stateOfHealthPercent; // Calculated SoH
    private Long totalSwapCount;
    private Double averageSocOnReturn;
    private Double degradationRate; // Percentage per month
    private InspectionCondition currentCondition;
    
    // Usage timeline
    private List<UsageEvent> usageTimeline;
    
    // Inspection history summary
    private List<InspectionSummary> inspectionHistory;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsageEvent {
        private LocalDateTime timestamp;
        private String action;
        private String stationName;
        private String notes;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InspectionSummary {
        private LocalDateTime inspectedAt;
        private InspectionCondition condition;
        private Integer socPercent;
        private String notes;
        private String inspectorName;
    }
}
