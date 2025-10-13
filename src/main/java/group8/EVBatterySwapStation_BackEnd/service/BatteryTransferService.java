package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.BatteryTransferRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryTransferDTO;
import group8.EVBatterySwapStation_BackEnd.enums.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BatteryTransferService {
    BatteryTransferDTO initiateTransfer(BatteryTransferRequest request);
    
    BatteryTransferDTO completeTransfer(Long transferId);
    
    BatteryTransferDTO cancelTransfer(Long transferId, String reason);
    
    Page<BatteryTransferDTO> listTransfers(Long stationId, TransferStatus status, Pageable pageable);
    
    BatteryTransferDTO getTransferById(Long transferId);
}
