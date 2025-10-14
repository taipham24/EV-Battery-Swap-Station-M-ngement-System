package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentType;
import lombok.Data;

@Data
public class PaymentRequest {
    // ---- Loại thanh toán ----
    private PaymentType paymentType; // SWAP hoặc SUBSCRIPTION

    // ---- ID giao dịch liên quan ----
    private Long swapId;             // nếu thanh toán theo lượt
    private Long subscriptionId;     // nếu thanh toán theo gói

    // ---- Hình thức thanh toán ----
    private PaymentMethod method;    // VNPAY, CASH, BANK_TRANSFER, ...
    private String ipAddr;           // Địa chỉ IP của khách hàng (nếu thanh toán online)
    private Long amountVnd;        // Số tiền thanh toán

}
