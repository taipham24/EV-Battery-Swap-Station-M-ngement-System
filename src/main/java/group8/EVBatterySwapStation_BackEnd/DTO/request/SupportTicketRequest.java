package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.IssueType;
import group8.EVBatterySwapStation_BackEnd.enums.Priority;
import group8.EVBatterySwapStation_BackEnd.enums.SupportCategory;
import lombok.Data;

@Data
public class SupportTicketRequest {
    private Long stationId;     // optional
    private Priority priority;     // ví dụ: "HIGH"
    private IssueType issueType;   // ví dụ: "Battery Issue"
    private SupportCategory category;
    private String description; // chi tiết sự cố
}
