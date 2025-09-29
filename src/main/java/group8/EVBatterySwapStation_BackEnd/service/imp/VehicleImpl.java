package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.Vehicle;
import group8.EVBatterySwapStation_BackEnd.repository.VehicleRepository;
import group8.EVBatterySwapStation_BackEnd.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleImpl implements VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public Vehicle registerVehicle(Vehicle vehicle) {
        if (vehicle.getVin() == null || vehicle.getVin().isEmpty()) {
            throw new IllegalArgumentException("VIN is required");
        }
        if (vehicle.getBatteryType() == null) {
            throw new IllegalArgumentException("Battery type is required");
        }
        return vehicleRepository.save(vehicle);
    }
}
