package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
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
public class RevenueReportResponse {
    private Long totalRevenue;
    private Map<String, Long> revenueByPeriod;
    private List<StationRevenueDTO> revenueByStation;
    private Map<PaymentMethod, Long> revenueByPaymentMethod;
    private Double revenueGrowth; // percentage change
    private String period; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    private String periodType;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationRevenueDTO {
        private Long stationId;
        private String stationName;
        private Long revenue;
        private Long swapCount;
    }
}
