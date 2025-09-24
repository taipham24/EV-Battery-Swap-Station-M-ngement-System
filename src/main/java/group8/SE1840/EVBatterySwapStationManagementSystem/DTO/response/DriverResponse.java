package group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long driverId;
    private String userName;
    private String email;
    private String fullName;
    private boolean status;
    private Set<RoleResponse> roles;
}
