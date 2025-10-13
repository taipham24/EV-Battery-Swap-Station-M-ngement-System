package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.BatteryTransferRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryTransferDTO;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryLedgerAction;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.enums.TransferStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.*;
import group8.EVBatterySwapStation_BackEnd.service.BatteryTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryTransferServiceImpl implements BatteryTransferService {
    
    private final BatteryTransferRepository batteryTransferRepository;
    private final BatteryRepository batteryRepository;
    private final StationRepository stationRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final BatteryLedgerRepository batteryLedgerRepository;

    @Override
    @Transactional
    public BatteryTransferDTO initiateTransfer(BatteryTransferRequest request) {
        // Validate battery exists and is transferable
        Battery battery = batteryRepository.findById(request.getBatteryId())
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
        
        if (battery.getStatus() != BatteryStatus.AVAILABLE && battery.getStatus() != BatteryStatus.MAINTENANCE) {
            throw new AppException(ErrorCode.BATTERY_CANNOT_BE_TRANSFERRED);
        }
        
        // Validate destination station exists
        Station toStation = stationRepository.findById(request.getToStationId())
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
        
        // Check if battery is already at destination station
        if (battery.getStation().getStationId().equals(request.getToStationId())) {
            throw new AppException(ErrorCode.BATTERY_CANNOT_BE_TRANSFERRED);
        }
        
        // Get current staff (initiated by)
        StaffProfile initiatedBy = getCurrentStaff();
        
        // Create transfer record
        BatteryTransfer transfer = BatteryTransfer.builder()
                .battery(battery)
                .fromStation(battery.getStation())
                .toStation(toStation)
                .initiatedBy(initiatedBy)
                .status(TransferStatus.INITIATED)
                .reason(request.getReason())
                .initiatedAt(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        
        transfer = batteryTransferRepository.save(transfer);
        
        return toDTO(transfer);
    }

    @Override
    @Transactional
    public BatteryTransferDTO completeTransfer(Long transferId) {
        BatteryTransfer transfer = batteryTransferRepository.findById(transferId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_NOT_FOUND));
        
        if (transfer.getStatus() != TransferStatus.INITIATED) {
            throw new AppException(ErrorCode.TRANSFER_ALREADY_COMPLETED);
        }
        
        // Get current staff (completed by)
        StaffProfile completedBy = getCurrentStaff();
        
        // Update battery station
        Battery battery = transfer.getBattery();
        Station fromStation = transfer.getFromStation();
        Station toStation = transfer.getToStation();
        
        BatteryStatus prevStatus = battery.getStatus();
        battery.setStation(toStation);
        batteryRepository.save(battery);
        
        // Log ledger entries
        logLedger(battery, fromStation, BatteryLedgerAction.TRANSFER_OUT, prevStatus, prevStatus, transferId);
        logLedger(battery, toStation, BatteryLedgerAction.TRANSFER_IN, prevStatus, prevStatus, transferId);
        
        // Update transfer record
        transfer.setCompletedBy(completedBy);
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());
        transfer = batteryTransferRepository.save(transfer);
        
        return toDTO(transfer);
    }

    @Override
    @Transactional
    public BatteryTransferDTO cancelTransfer(Long transferId, String reason) {
        BatteryTransfer transfer = batteryTransferRepository.findById(transferId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_NOT_FOUND));
        
        if (transfer.getStatus() != TransferStatus.INITIATED) {
            throw new AppException(ErrorCode.TRANSFER_ALREADY_COMPLETED);
        }
        
        // Update transfer record
        transfer.setStatus(TransferStatus.CANCELLED);
        transfer.setCompletedAt(LocalDateTime.now());
        transfer.setNotes(transfer.getNotes() + " [CANCELLED: " + reason + "]");
        transfer = batteryTransferRepository.save(transfer);
        
        return toDTO(transfer);
    }

    @Override
    public Page<BatteryTransferDTO> listTransfers(Long stationId, TransferStatus status, Pageable pageable) {
        Page<BatteryTransfer> transfers;
        
        if (stationId != null && status != null) {
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
            transfers = batteryTransferRepository.findByFromStationOrToStationAndStatus(station, station, status, pageable);
        } else if (stationId != null) {
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
            transfers = batteryTransferRepository.findByFromStationOrToStation(station, station, pageable);
        } else if (status != null) {
            transfers = batteryTransferRepository.findByStatus(status, pageable);
        } else {
            transfers = batteryTransferRepository.findAll(pageable);
        }
        
        return transfers.map(this::toDTO);
    }

    @Override
    public BatteryTransferDTO getTransferById(Long transferId) {
        BatteryTransfer transfer = batteryTransferRepository.findById(transferId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_NOT_FOUND));
        
        return toDTO(transfer);
    }

    private void logLedger(Battery battery, Station station, BatteryLedgerAction action,
                           BatteryStatus prevStatus, BatteryStatus newStatus, Long refTransferId) {
        BatteryLedger entry = BatteryLedger.builder()
                .battery(battery)
                .station(station)
                .action(action)
                .prevStatus(prevStatus)
                .newStatus(newStatus)
                .refSwapId(refTransferId) // Reusing refSwapId field for transfer reference
                .createdAt(LocalDateTime.now())
                .build();
        batteryLedgerRepository.save(entry);
    }

    private StaffProfile getCurrentStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENDICATED);
        }
        
        String username = authentication.getName();
        // For now, we'll need to find staff by username - this is a simplified approach
        // In a real system, you'd have proper user context management
        List<StaffProfile> staffProfiles = staffProfileRepository.findAll();
        return staffProfiles.stream()
                .filter(staff -> staff.getDriver().getUserName().equals(username))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    private BatteryTransferDTO toDTO(BatteryTransfer transfer) {
        return BatteryTransferDTO.builder()
                .transferId(transfer.getTransferId())
                .batteryId(transfer.getBattery().getBatteryId())
                .batterySerialNumber(transfer.getBattery().getSerialNumber())
                .fromStationId(transfer.getFromStation().getStationId())
                .fromStationName(transfer.getFromStation().getName())
                .toStationId(transfer.getToStation().getStationId())
                .toStationName(transfer.getToStation().getName())
                .initiatedByStaffId(transfer.getInitiatedBy().getStaffId())
                .initiatedByStaffName(transfer.getInitiatedBy().getDriver().getFullName())
                .completedByStaffId(transfer.getCompletedBy() != null ? transfer.getCompletedBy().getStaffId() : null)
                .completedByStaffName(transfer.getCompletedBy() != null ? transfer.getCompletedBy().getDriver().getFullName() : null)
                .status(transfer.getStatus())
                .reason(transfer.getReason())
                .initiatedAt(transfer.getInitiatedAt())
                .completedAt(transfer.getCompletedAt())
                .notes(transfer.getNotes())
                .build();
    }
}
