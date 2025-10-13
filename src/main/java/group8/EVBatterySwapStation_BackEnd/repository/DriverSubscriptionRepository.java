package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverSubscriptionRepository extends JpaRepository<DriverSubscription, Long> {
    Optional<DriverSubscription> findByDriverAndActiveTrue(Driver driver);

    List<DriverSubscription> findByDriver(Driver driver);

    // Customer management queries
    @Query("SELECT ds FROM DriverSubscription ds WHERE ds.driver.driverId = :driverId ORDER BY ds.startDate DESC")
    List<DriverSubscription> findByDriverIdOrderByStartDateDesc(@Param("driverId") Long driverId);

    @Query("SELECT COUNT(ds) FROM DriverSubscription ds WHERE ds.active = true")
    long countActiveSubscriptions();

    Page<DriverSubscription> findByPlan(SubscriptionPlan plan, Pageable pageable);
}
