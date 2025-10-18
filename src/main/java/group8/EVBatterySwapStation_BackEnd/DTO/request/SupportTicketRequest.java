package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Data;

@Data
public class SupportTicketRequest {
    private Long stationId;     // optional
    private String issueType;   // ví dụ: "Battery Issue"
    private String description; // chi tiết sự cố
}
