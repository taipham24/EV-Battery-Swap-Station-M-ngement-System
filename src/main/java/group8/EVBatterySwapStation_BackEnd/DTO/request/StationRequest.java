package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.StationStatus;
import lombok.Data;

@Data
public class StationRequest {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int capacity;
    private StationStatus status;
}
