package group8.SE1840.EVBatterySwapStationManagementSystem.service;

import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.DriverDTO;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.DriverResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Vehicle;

import java.util.List;

public interface DriverService {
    DriverResponse register(DriverDTO driverRequest, String userRoleChoice);

    List<DriverResponse> getAllDrivers();

    DriverResponse getUser(Long driverId);

    DriverResponse getMyInfo();

    DriverResponse updateDriver(Long driverId, DriverDTO newInfoDriver);

    Vehicle addVehicleToDriver(Long driverId, String vin, String batteryType);

    Vehicle getVehicleByDriver(Long driverId);
}
