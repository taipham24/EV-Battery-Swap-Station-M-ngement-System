package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDTO {
    private Long paymentId;
    private Long swapId;
    private PaymentMethod method;
    private Long amountVnd;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private Long cashierStaffId;
}



