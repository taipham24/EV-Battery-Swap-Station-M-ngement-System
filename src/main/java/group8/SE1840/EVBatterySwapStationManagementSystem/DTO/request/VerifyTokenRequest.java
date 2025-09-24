package group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerifyTokenRequest {
    private String token;
}
