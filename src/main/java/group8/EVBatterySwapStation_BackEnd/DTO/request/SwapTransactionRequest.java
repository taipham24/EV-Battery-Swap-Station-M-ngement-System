package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SwapTransactionRequest {
    private Long bookingId;       // nullable
    private Long driverId;
    private Long stationId;
    @NotNull
    private Long oldBatteryId;
    @NotNull
    private Long newBatteryId;
    private Long staffId;
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
}
