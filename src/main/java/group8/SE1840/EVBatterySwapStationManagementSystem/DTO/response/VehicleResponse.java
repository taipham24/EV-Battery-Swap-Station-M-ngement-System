package group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private Long vehicleId;
    private String vin;
    private String manufacturer;
    private String model;
    private int manufactureYear;
    private String batteryType;
    private Long driverId;
    private String driverName;
}
