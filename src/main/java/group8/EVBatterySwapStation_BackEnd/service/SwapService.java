package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.InspectReturnRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.SwapPaymentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryInspectionDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PaymentDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapTransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SwapService {
    SwapTransactionDTO confirm(Long bookingId);
    PaymentDTO pay(Long swapId, SwapPaymentRequest request);
    BatteryInspectionDTO inspectReturn(Long swapId, InspectReturnRequest request);
    Page<SwapTransactionDTO> list(Long driverId, Long stationId, String status, Pageable pageable);
}




