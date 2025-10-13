package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.InspectionCondition;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BatteryInspectionDTO {
    private Long inspectionId;
    private Long swapId;
    private Long batteryId;
    private InspectionCondition condition;
    private Integer socPercent;
    private String notes;
    private Long inspectorStaffId;
    private LocalDateTime inspectedAt;
}




