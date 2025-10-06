package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private BatteryStatus status;
    private String reason;
    private boolean adminOverride;
}
