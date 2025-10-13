package group8.EVBatterySwapStation_BackEnd.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistoryResponse {
    private Long subscriptionId;
    private String planName;
    private String batterySerial;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int swapsUsed;
    private int swapLimit;
    private boolean active;
    private boolean autoRenew;
}
