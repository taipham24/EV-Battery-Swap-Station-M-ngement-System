package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.DriverResponse;

import java.util.List;

public interface DriverService {


    DriverResponse register(DriverDTO driverRequest, String userRoleChoice);

    List<DriverResponse> getAllDrivers();

    DriverResponse getUser(Long driverId);

    DriverResponse getMyInfo();

    DriverResponse updateDriver(Long driverId, DriverDTO newInfoDriver);

    void registerBatterySwapService(Long driverId);
}
