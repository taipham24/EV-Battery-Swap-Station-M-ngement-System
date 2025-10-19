package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.AssignTicketRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffAssignmentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffFilterRequest;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StaffDetailResponse;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.service.StaffManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Staff Management", description = "Admin endpoints for staff management")
@PreAuthorize("hasAuthority('ADMIN')")
public class StaffManagementController {

    private final StaffManagementService staffManagementService;

    @PostMapping("/assign")
    @Operation(summary = "Assign staff to station")
    public ResponseEntity<ApiResponse<StaffDetailResponse>> assignStaffToStation(
            @Parameter(description = "Staff assignment data") @Valid @RequestBody StaffAssignmentRequest request) {
        log.info("Admin assigning staff {} to station {}", request.getDriverId(), request.getStationId());

        StaffDetailResponse staff = staffManagementService.assignStaffToStation(request);

        return ResponseEntity.ok(ApiResponse.<StaffDetailResponse>builder()
                .code(200)
                .message("Staff assigned successfully")
                .result(staff)
                .build());
    }

    @PutMapping("/{staffId}")
    @Operation(summary = "Update staff assignment")
    public ResponseEntity<ApiResponse<StaffDetailResponse>> updateStaffAssignment(
            @Parameter(description = "Staff ID") @PathVariable Long staffId,
            @Parameter(description = "Staff assignment data") @Valid @RequestBody StaffAssignmentRequest request) {
        log.info("Admin updating staff assignment {} with data: {}", staffId, request);

        StaffDetailResponse staff = staffManagementService.updateStaffAssignment(staffId, request);

        return ResponseEntity.ok(ApiResponse.<StaffDetailResponse>builder()
                .code(200)
                .message("Staff assignment updated successfully")
                .result(staff)
                .build());
    }

    @DeleteMapping("/{staffId}")
    @Operation(summary = "Remove staff from station")
    public ResponseEntity<ApiResponse<Void>> removeStaffFromStation(
            @Parameter(description = "Staff ID") @PathVariable Long staffId) {
        log.info("Admin removing staff {} from station", staffId);

        staffManagementService.removeStaffFromStation(staffId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Staff removed from station successfully")
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all staff with filtering")
    public ResponseEntity<ApiResponse<Page<StaffDetailResponse>>> getAllStaff(
            @Parameter(description = "Filter criteria") StaffFilterRequest filter) {
        log.info("Admin getting all staff with filter: {}", filter);

        Page<StaffDetailResponse> staff = staffManagementService.getAllStaff(filter);

        return ResponseEntity.ok(ApiResponse.<Page<StaffDetailResponse>>builder()
                .code(200)
                .message("Staff retrieved successfully")
                .result(staff)
                .build());
    }

    @GetMapping("/{staffId}")
    @Operation(summary = "Get staff detail by ID")
    public ResponseEntity<ApiResponse<StaffDetailResponse>> getStaffDetail(
            @Parameter(description = "Staff ID") @PathVariable Long staffId) {
        log.info("Admin getting staff detail for ID: {}", staffId);

        StaffDetailResponse staff = staffManagementService.getStaffDetail(staffId);

        return ResponseEntity.ok(ApiResponse.<StaffDetailResponse>builder()
                .code(200)
                .message("Staff detail retrieved successfully")
                .result(staff)
                .build());
    }

    @GetMapping("/station/{stationId}")
    @Operation(summary = "Get staff at specific station")
    public ResponseEntity<ApiResponse<List<StaffDetailResponse>>> getStationStaff(
            @Parameter(description = "Station ID") @PathVariable Long stationId) {
        log.info("Admin getting staff for station: {}", stationId);

        List<StaffDetailResponse> staff = staffManagementService.getStationStaff(stationId);

        return ResponseEntity.ok(ApiResponse.<List<StaffDetailResponse>>builder()
                .code(200)
                .message("Station staff retrieved successfully")
                .result(staff)
                .build());
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get staff statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStaffStatistics() {
        log.info("Admin getting staff statistics");

        Map<String, Object> statistics = staffManagementService.getStaffStatistics();

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Staff statistics retrieved successfully")
                .result(statistics)
                .build());
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAuthority('STAFF')")
    @Operation(summary = "Get tickets handled by staff")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getAllTickets() {
        log.info("Staff getting all tickets handled by staff");

        List<SupportTicket> tickets = staffManagementService.getAllTickets();

        return ResponseEntity.ok(ApiResponse.<List<SupportTicket>>builder()
                .code(200)
                .message("Tickets retrieved successfully")
                .result(tickets)
                .build());
    }

    @PutMapping("/{ticketId}/assign")
    @PreAuthorize("hasAuthority('STAFF')")
    @Operation(summary = "Assign ticket to staff")
    public ResponseEntity<ApiResponse<SupportTicket>> assignTicketToStaff(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "Staff ID") @RequestParam AssignTicketRequest request) {
        log.info("Staff assigning ticket {} to staff {}", ticketId, request);
        SupportTicket ticket = staffManagementService.assignTicket(ticketId, request);
        return ResponseEntity.ok(ApiResponse.<SupportTicket>builder()
                .code(200)
                .message("Ticket assigned successfully")
                .result(ticket)
                .build());
    }
}
