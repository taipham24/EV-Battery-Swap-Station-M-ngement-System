package group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {
    private String vin;
    private String batteryType;
    private String model;
    private String manufacturer;
    private int manufactureYear;
}
