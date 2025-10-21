package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.IssueType;
import group8.EVBatterySwapStation_BackEnd.enums.Priority;
import group8.EVBatterySwapStation_BackEnd.enums.SupportCategory;
import group8.EVBatterySwapStation_BackEnd.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SupportTicketResponse {
    private Long ticketId;
    private Long driverId;
    private Long stationId;     // optional
    private IssueType issueType;   // e.g., "Battery Issue"
    private String description; // detailed description of the issue
    private TicketStatus status;      // e.g., "OPEN", "RESOLVED"
    private Priority priority;     // e.g., "HIGH"
    private SupportCategory category;
    private String stationName;
    private String assignedStaff;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

}
