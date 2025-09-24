package group8.SE1840.EVBatterySwapStationManagementSystem.mapper;

import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.VehicleRequest;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.VehicleResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Vehicle;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {
    @Builder
    public static VehicleRequest mapToVehicleRequest(Vehicle vehicle) {
        return new VehicleRequest(
                vehicle.getVin(),
                vehicle.getBatteryType(),
                vehicle.getModel(),
                vehicle.getManufacturer(),
                vehicle.getManufactureYear()
        );
    }

    public static Vehicle mapToVehicle(VehicleRequest vehicleRequest) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(vehicleRequest.getVin());
        vehicle.setBatteryType(vehicleRequest.getBatteryType());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setManufacturer(vehicleRequest.getManufacturer());
        vehicle.setManufactureYear(vehicleRequest.getManufactureYear());
        return vehicle;
    }

    public static VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        Long driverId = (vehicle.getDriver() != null) ? vehicle.getDriver().getDriverId() : null;
        String driverName = (vehicle.getDriver() != null) ? vehicle.getDriver().getFullName() : null;
        return new VehicleResponse(
                vehicle.getVehicleId(),
                vehicle.getVin(),
                vehicle.getManufacturer(),
                vehicle.getModel(),
                vehicle.getManufactureYear(),
                vehicle.getBatteryType(),
                driverId,
                driverName
        );
    }
}
