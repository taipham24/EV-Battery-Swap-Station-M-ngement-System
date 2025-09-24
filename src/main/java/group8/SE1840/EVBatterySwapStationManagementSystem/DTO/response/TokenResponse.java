package group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private Date expirationTime;
}
