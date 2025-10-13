package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapAnalyticsResponse {
    private Long totalSwaps;
    private Map<String, Long> swapsByPeriod;
    private List<StationSwapDTO> swapsByStation;
    private Double averageSwapsPerDay;
    private List<HourlySwapDTO> peakHours;
    private Map<String, Long> heatmapData; // hour x day of week matrix
    private Map<SwapStatus, Long> swapsByStatus;
    private String period;
    private String periodType;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationSwapDTO {
        private Long stationId;
        private String stationName;
        private Long swapCount;
        private Long revenue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlySwapDTO {
        private int hour; // 0-23
        private Long swapCount;
        private Long averageRevenue;
    }
}
