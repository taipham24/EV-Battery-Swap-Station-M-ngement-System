package group8.SE1840.EVBatterySwapStationManagementSystem.repository;

import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByUserName(String userName);

    Optional<Driver> findByUserName(String userName);

    boolean existsByEmail(String email);


}
