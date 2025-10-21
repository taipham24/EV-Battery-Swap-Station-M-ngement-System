package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.AssignTicketRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffAssignmentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StaffDetailResponse;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.enums.SupportCategory;
import group8.EVBatterySwapStation_BackEnd.enums.TicketStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface StaffManagementService {

    /**
     * Assign staff to station
     */
    StaffDetailResponse assignStaffToStation(StaffAssignmentRequest request);

    /**
     * Update staff assignment
     */
    StaffDetailResponse updateStaffAssignment(Long staffId, StaffAssignmentRequest request);

    /**
     * Remove staff from station
     */
    void removeStaffFromStation(Long staffId);

    /**
     * Get all staff with filtering
     */
    Page<StaffDetailResponse> getAllStaff(StaffFilterRequest filter);

    /**
     * Get staff detail by ID
     */
    StaffDetailResponse getStaffDetail(Long staffId);

    /**
     * Get staff at specific station
     */
    List<StaffDetailResponse> getStationStaff(Long stationId);

    /**
     * Get staff statistics
     */
    Map<String, Object> getStaffStatistics();

    List<SupportTicket> getAllTickets();

    SupportTicket assignTicket(Long ticketId, AssignTicketRequest request);

    List<SupportTicket> filterTickets(SupportCategory category, TicketStatus status, Long stationId);

    SupportTicket updateStatusAndResponse(Long ticketId, TicketStatus status, String response);

    void checkSlaAndEscalate();

    Map<String, Object> getDashboardStats();
}
