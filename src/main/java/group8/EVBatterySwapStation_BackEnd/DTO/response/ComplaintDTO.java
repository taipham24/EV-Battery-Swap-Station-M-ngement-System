package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintDTO {
    private Long complaintId;
    private Long driverId;
    private String driverName;
    private Long swapId;
    private Long batteryId;
    private String batterySerialNumber;
    private Long stationId;
    private String stationName;
    private ComplaintType type;
    private ComplaintStatus status;
    private String subject;
    private String description;
    private String resolution;
    private Long reviewedByStaffId;
    private String reviewedByStaffName;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime resolvedAt;
}
