package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;

import java.util.List;

public interface BatteryService {
    Battery addBatteryToStation(Long stationId, BatteryStatus status);

    List<Battery> getBatteriesByStation(Long stationId, BatteryStatus status);
}
