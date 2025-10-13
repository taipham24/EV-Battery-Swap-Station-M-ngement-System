package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SwapTransactionDTO {
    private Long swapId;
    private Long driverId;
    private Long stationId;
    private Long reservedBatteryId;
    private Long returnedBatteryId;
    private SwapStatus status;
    private Long amountVnd;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime paidAt;
    private LocalDateTime inspectedAt;
    private LocalDateTime completedAt;
    private String notes;
}




