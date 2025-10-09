package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.InspectionCondition;
import lombok.Data;

@Data
public class InspectReturnRequest {
    private Long batteryId;
    private InspectionCondition condition;
    private Integer socPercent;
    private String notes;
}



