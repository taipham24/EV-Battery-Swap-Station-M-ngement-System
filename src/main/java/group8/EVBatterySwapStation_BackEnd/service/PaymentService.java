package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.PaymentRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface PaymentService {
    String createPayment(PaymentRequest request);

    boolean verifyPayment(Map<String, String> params) throws UnsupportedEncodingException;
}
