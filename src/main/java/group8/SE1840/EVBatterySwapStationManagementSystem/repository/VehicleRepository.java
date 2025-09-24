package group8.SE1840.EVBatterySwapStationManagementSystem.repository;

import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVin(String vin);

    Optional<Vehicle> findByDriver_DriverId(Long driverId); // tìm xe theo DriverId
}
