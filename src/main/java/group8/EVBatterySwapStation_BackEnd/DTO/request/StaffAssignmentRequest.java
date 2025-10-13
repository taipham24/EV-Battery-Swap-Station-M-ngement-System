package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffAssignmentRequest {
    private Long driverId; // must have STAFF role
    private Long stationId;
    private String workShift;
    private String notes;
    private Boolean active;
}
