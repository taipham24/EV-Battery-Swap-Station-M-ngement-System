package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffFilterRequest {
    private Long stationId;
    private Boolean active;
    private String workShift;
    private String keyword; // search in userName, fullName, email
    private String sortBy = "assignedDate";
    private String sortDirection = "desc";
    private int page = 0;
    private int size = 20;
}
