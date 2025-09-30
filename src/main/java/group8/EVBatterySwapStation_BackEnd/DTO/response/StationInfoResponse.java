package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.StationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationInfoResponse {
    private Long stationId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int capacity;
    private StationStatus status;
    private long availableBatteries;
}
