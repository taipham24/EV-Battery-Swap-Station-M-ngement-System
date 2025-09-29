package group8.EVBatterySwapStation_BackEnd.DTO.response;

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
