package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.ComplaintRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.ComplaintResolutionRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.ComplaintDTO;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintType;
import group8.EVBatterySwapStation_BackEnd.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    
    private final ComplaintService complaintService;

    @PostMapping("")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<ComplaintDTO> submitComplaint(@Valid @RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.submitComplaint(request));
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ComplaintDTO>> listComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(complaintService.listComplaints(status, type, pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Page<ComplaintDTO>> getMyComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        // For now, we'll get the current driver ID from security context
        // In a real implementation, you'd extract this from JWT token
        Long driverId = getCurrentDriverId();
        return ResponseEntity.ok(complaintService.getComplaintsByDriver(driverId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<ComplaintDTO> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintDTO> updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(id, status));
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintDTO> resolveComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintResolutionRequest request) {
        return ResponseEntity.ok(complaintService.resolveComplaint(id, request));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getComplaintStatistics() {
        return ResponseEntity.ok(complaintService.getComplaintStatistics());
    }

    // Helper method to get current driver ID - simplified implementation
    private Long getCurrentDriverId() {
        // In a real implementation, extract from JWT token or security context
        // For now, return a default value or throw an exception
        throw new RuntimeException("Driver ID extraction not implemented");
    }
}
