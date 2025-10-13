package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.TransferStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryTransferDTO {
    private Long transferId;
    private Long batteryId;
    private String batterySerialNumber;
    private Long fromStationId;
    private String fromStationName;
    private Long toStationId;
    private String toStationName;
    private Long initiatedByStaffId;
    private String initiatedByStaffName;
    private Long completedByStaffId;
    private String completedByStaffName;
    private TransferStatus status;
    private String reason;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String notes;
}
