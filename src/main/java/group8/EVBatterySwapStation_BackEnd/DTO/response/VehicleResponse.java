package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.BatteryType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private Long vehicleId;
    private String vin;
    private BatteryType batteryType;   // trả ra String cho dễ đọc (Enum toString())
    private Long driverId;


}
