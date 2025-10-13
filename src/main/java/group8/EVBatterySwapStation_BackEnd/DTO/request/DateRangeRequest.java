package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DateRangeRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String period; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    private Long stationId; // optional filter
    private String groupBy; // HOUR, DAY, WEEK, MONTH, YEAR
}
