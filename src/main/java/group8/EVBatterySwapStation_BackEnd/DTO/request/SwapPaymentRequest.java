package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import lombok.Data;

@Data
public class SwapPaymentRequest {
    private PaymentMethod method;
    private Long amountVnd;
}



