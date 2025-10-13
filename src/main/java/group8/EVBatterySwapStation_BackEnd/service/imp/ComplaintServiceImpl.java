package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.ComplaintRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.ComplaintResolutionRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.ComplaintDTO;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintType;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.*;
import group8.EVBatterySwapStation_BackEnd.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
    
    private final ComplaintRepository complaintRepository;
    private final DriverRepository driverRepository;
    private final SwapTransactionRepository swapTransactionRepository;
    private final BatteryRepository batteryRepository;
    private final StationRepository stationRepository;
    private final StaffProfileRepository staffProfileRepository;

    @Override
    @Transactional
    public ComplaintDTO submitComplaint(ComplaintRequest request) {
        // Get current driver
        Driver driver = getCurrentDriver();
        
        // Validate related entities if provided
        SwapTransaction swap = null;
        if (request.getSwapId() != null) {
            swap = swapTransactionRepository.findById(request.getSwapId())
                    .orElseThrow(() -> new AppException(ErrorCode.SWAP_NOT_FOUND));
        }
        
        Battery battery = null;
        if (request.getBatteryId() != null) {
            battery = batteryRepository.findById(request.getBatteryId())
                    .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
        }
        
        Station station = null;
        if (request.getStationId() != null) {
            station = stationRepository.findById(request.getStationId())
                    .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
        }
        
        // Create complaint
        Complaint complaint = Complaint.builder()
                .driver(driver)
                .swap(swap)
                .battery(battery)
                .station(station)
                .type(request.getType())
                .status(ComplaintStatus.SUBMITTED)
                .subject(request.getSubject())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        
        complaint = complaintRepository.save(complaint);
        
        return toDTO(complaint);
    }

    @Override
    @Transactional
    public ComplaintDTO updateComplaintStatus(Long id, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLAINT_NOT_FOUND));
        
        if (complaint.getStatus() == ComplaintStatus.RESOLVED || complaint.getStatus() == ComplaintStatus.REJECTED) {
            throw new AppException(ErrorCode.COMPLAINT_ALREADY_RESOLVED);
        }
        
        complaint.setStatus(status);
        complaint.setReviewedAt(LocalDateTime.now());
        complaint.setReviewedBy(getCurrentStaff());
        
        complaint = complaintRepository.save(complaint);
        
        return toDTO(complaint);
    }

    @Override
    @Transactional
    public ComplaintDTO resolveComplaint(Long id, ComplaintResolutionRequest request) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLAINT_NOT_FOUND));
        
        if (complaint.getStatus() == ComplaintStatus.RESOLVED || complaint.getStatus() == ComplaintStatus.REJECTED) {
            throw new AppException(ErrorCode.COMPLAINT_ALREADY_RESOLVED);
        }
        
        if (request.getNewStatus() != ComplaintStatus.RESOLVED && request.getNewStatus() != ComplaintStatus.REJECTED) {
            throw new AppException(ErrorCode.INVALID_COMPLAINT_STATUS);
        }
        
        complaint.setStatus(request.getNewStatus());
        complaint.setResolution(request.getResolution());
        complaint.setReviewedAt(LocalDateTime.now());
        complaint.setResolvedAt(LocalDateTime.now());
        complaint.setReviewedBy(getCurrentStaff());
        
        // Handle battery replacement if specified
        if (request.getReplacementBatteryId() != null && complaint.getBattery() != null) {
            Battery replacementBattery = batteryRepository.findById(request.getReplacementBatteryId())
                    .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
            
            // Mark old battery as damaged/quarantined
            complaint.getBattery().setStatus(group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus.DAMAGED);
            batteryRepository.save(complaint.getBattery());
            
            // Update replacement battery status to available
            replacementBattery.setStatus(group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus.AVAILABLE);
            batteryRepository.save(replacementBattery);
        }
        
        complaint = complaintRepository.save(complaint);
        
        return toDTO(complaint);
    }

    @Override
    public Page<ComplaintDTO> listComplaints(ComplaintStatus status, ComplaintType type, Pageable pageable) {
        Page<Complaint> complaints;
        
        if (status != null && type != null) {
            complaints = complaintRepository.findByStatusAndType(status, type, pageable);
        } else if (status != null) {
            complaints = complaintRepository.findByStatus(status, pageable);
        } else if (type != null) {
            complaints = complaintRepository.findByType(type, pageable);
        } else {
            complaints = complaintRepository.findAll(pageable);
        }
        
        return complaints.map(this::toDTO);
    }

    @Override
    public Page<ComplaintDTO> getComplaintsByDriver(Long driverId, Pageable pageable) {
        Page<Complaint> complaints = complaintRepository.findByDriverId(driverId, pageable);
        return complaints.map(this::toDTO);
    }

    @Override
    public Map<String, Object> getComplaintStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalComplaints", complaintRepository.count());
        stats.put("submittedComplaints", complaintRepository.countByStatus(ComplaintStatus.SUBMITTED));
        stats.put("inReviewComplaints", complaintRepository.countByStatus(ComplaintStatus.IN_REVIEW));
        stats.put("resolvedComplaints", complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        stats.put("rejectedComplaints", complaintRepository.countByStatus(ComplaintStatus.REJECTED));
        
        // Count by type
        Map<ComplaintType, Long> typeBreakdown = new HashMap<>();
        for (ComplaintType type : ComplaintType.values()) {
            typeBreakdown.put(type, complaintRepository.countByType(type));
        }
        stats.put("typeBreakdown", typeBreakdown);
        
        stats.put("generatedAt", LocalDateTime.now());
        
        return stats;
    }

    @Override
    public ComplaintDTO getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLAINT_NOT_FOUND));
        
        return toDTO(complaint);
    }

    private Driver getCurrentDriver() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENDICATED);
        }
        
        String username = authentication.getName();
        return driverRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    private StaffProfile getCurrentStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENDICATED);
        }
        
        String username = authentication.getName();
        return staffProfileRepository.findAll().stream()
                .filter(staff -> staff.getDriver().getUserName().equals(username))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    private ComplaintDTO toDTO(Complaint complaint) {
        return ComplaintDTO.builder()
                .complaintId(complaint.getComplaintId())
                .driverId(complaint.getDriver().getDriverId())
                .driverName(complaint.getDriver().getFullName())
                .swapId(complaint.getSwap() != null ? complaint.getSwap().getSwapId() : null)
                .batteryId(complaint.getBattery() != null ? complaint.getBattery().getBatteryId() : null)
                .batterySerialNumber(complaint.getBattery() != null ? complaint.getBattery().getSerialNumber() : null)
                .stationId(complaint.getStation() != null ? complaint.getStation().getStationId() : null)
                .stationName(complaint.getStation() != null ? complaint.getStation().getName() : null)
                .type(complaint.getType())
                .status(complaint.getStatus())
                .subject(complaint.getSubject())
                .description(complaint.getDescription())
                .resolution(complaint.getResolution())
                .reviewedByStaffId(complaint.getReviewedBy() != null ? complaint.getReviewedBy().getStaffId() : null)
                .reviewedByStaffName(complaint.getReviewedBy() != null ? complaint.getReviewedBy().getDriver().getFullName() : null)
                .createdAt(complaint.getCreatedAt())
                .reviewedAt(complaint.getReviewedAt())
                .resolvedAt(complaint.getResolvedAt())
                .build();
    }
}
