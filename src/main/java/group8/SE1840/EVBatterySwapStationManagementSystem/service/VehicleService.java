package group8.SE1840.EVBatterySwapStationManagementSystem.service;

import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.VehicleRequest;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.VehicleResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Driver;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Vehicle;
import group8.SE1840.EVBatterySwapStationManagementSystem.exception.AppException;
import group8.SE1840.EVBatterySwapStationManagementSystem.exception.ErrorCode;
import group8.SE1840.EVBatterySwapStationManagementSystem.mapper.VehicleMapper;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.DriverRepository;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    private static final int BATTERY_LIFETIME_YEARS = 5;

    @Transactional
    public VehicleResponse registerVehicle(Long driverId, VehicleRequest vehicleRequest) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        if (vehicleRepository.findByDriver_DriverId(driverId).isPresent()) {
            throw new AppException(ErrorCode.VEHICLE_ALREADY_REGISTERED);
        }
        Vehicle vehicle = Vehicle.builder()
                .vin(vehicleRequest.getVin())
                .manufacturer(vehicleRequest.getManufacturer())
                .model(vehicleRequest.getModel())
                .manufactureYear(vehicleRequest.getManufactureYear())
                .batteryType(vehicleRequest.getBatteryType())
                .driver(driver)
                .build();
        vehicleRepository.save(vehicle);
        return VehicleMapper.mapToVehicleResponse(vehicle);
    }


}
