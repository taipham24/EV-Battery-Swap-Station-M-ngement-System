package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    List<StaffProfile> findByStation_StationId(Long stationId);
}
