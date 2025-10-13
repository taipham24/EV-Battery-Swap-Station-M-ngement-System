package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.enums.StationStatus;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDetailResponse {
    private Long stationId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int capacity;
    private StationStatus status;
    private String imageUrl;
    
    // Battery inventory breakdown by status
    private Map<BatteryStatus, Long> batteryInventory;
    private long totalBatteries;
    private long availableBatteries;
    
    // Assigned staff information
    private List<StaffInfo> assignedStaff;
    
    // Recent swap activity summary
    private SwapActivitySummary recentActivity;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StaffInfo {
        private Long staffId;
        private String staffName;
        private String workShift;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SwapActivitySummary {
        private long swapsToday;
        private long swapsThisWeek;
        private long swapsThisMonth;
        private double averageSwapTimeMinutes;
    }
}
