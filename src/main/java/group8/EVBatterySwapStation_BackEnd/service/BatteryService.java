package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;

public interface BatteryService {
    Battery addBatteryToStation(Long stationId, BatteryStatus status);
}
