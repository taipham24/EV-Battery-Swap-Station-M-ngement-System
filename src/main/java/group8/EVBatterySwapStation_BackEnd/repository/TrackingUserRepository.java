package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.TrackingUserLogin;
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
