package group8.EVBatterySwapStation_BackEnd.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDetailResponse {
    private Long staffId;
    private Long driverId;
    private String userName;
    private String fullName;
    private String email;
    private Long stationId;
    private String stationName;
    private String workShift;
    private LocalDateTime assignedDate;
    private boolean active;
    private String notes;
}
