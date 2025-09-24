package group8.SE1840.EVBatterySwapStationManagementSystem.repository;

import group8.SE1840.EVBatterySwapStationManagementSystem.entity.TrackingUserLogin;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingUserRepository extends JpaRepository<TrackingUserLogin, Long> {

    @Modifying
    @Transactional
    void deleteByDriverId(Long driverId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TrackingUserLogin")
    void deleteAllTrackingUsers();
}
