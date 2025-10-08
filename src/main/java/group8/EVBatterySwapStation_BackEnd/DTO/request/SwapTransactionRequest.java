package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SwapTransactionRequest {
    private Long bookingId;       // nullable
    private Long driverId;
    private Long stationId;
    private Long oldBatteryId;
    private Long newBatteryId;
    private Long staffId;
    private LocalDateTime swapTime;
    private Long paymentId;
}
