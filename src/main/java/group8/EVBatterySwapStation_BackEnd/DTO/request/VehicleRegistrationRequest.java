package group8.EVBatterySwapStation_BackEnd.DTO.request;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryType;
import lombok.Data;

@Data
public class VehicleRegistrationRequest {
    private String vin;
    private BatteryType batteryType;

}
