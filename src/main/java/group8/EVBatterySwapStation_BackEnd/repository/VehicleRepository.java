package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVin(String vin);
    boolean existsByVin(String vin);
    boolean existsByDriver(Driver driver);
    boolean existsByDriverAndVin(Driver driver, String vin);
    List<Vehicle> findByDriver(Driver driver);
    Optional<Vehicle> findByVinAndDriverIsNull(String vin);

}
