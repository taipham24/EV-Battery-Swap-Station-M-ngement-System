package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.PaymentRequest;

public interface PaymentService {
    String createPayment(PaymentRequest request);
}
