package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverSubscriptionRepository extends JpaRepository<DriverSubscription, Long> {
    Optional<DriverSubscription> findByDriverAndActiveTrue(Driver driver);

    List<DriverSubscription> findByDriver(Driver driver);
}
