package group8.EVBatterySwapStation_BackEnd.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeakHourAnalysis {
    private int hour; // 0-23
    private int dayOfWeek; // 1-7
    private Long swapCount;
    private Long averageRevenue;
}
