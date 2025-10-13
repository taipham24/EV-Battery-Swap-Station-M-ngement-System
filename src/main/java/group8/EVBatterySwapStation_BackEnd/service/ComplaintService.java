package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.ComplaintRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.ComplaintResolutionRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.ComplaintDTO;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ComplaintService {
    ComplaintDTO submitComplaint(ComplaintRequest request);
    
    ComplaintDTO updateComplaintStatus(Long id, ComplaintStatus status);
    
    ComplaintDTO resolveComplaint(Long id, ComplaintResolutionRequest request);
    
    Page<ComplaintDTO> listComplaints(ComplaintStatus status, ComplaintType type, Pageable pageable);
    
    Page<ComplaintDTO> getComplaintsByDriver(Long driverId, Pageable pageable);
    
    Map<String, Object> getComplaintStatistics();
    
    ComplaintDTO getComplaintById(Long id);
}
