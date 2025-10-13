package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.StaffProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    List<StaffProfile> findByStation_StationId(Long stationId);

    // Staff management queries
    @Query("SELECT sp FROM StaffProfile sp WHERE " +
           "(sp.station.stationId = :stationId OR :stationId IS NULL) AND " +
           "(sp.active = :active OR :active IS NULL) AND " +
           "(sp.workShift = :workShift OR :workShift IS NULL)")
    Page<StaffProfile> findWithFilters(
        @Param("stationId") Long stationId,
        @Param("active") Boolean active,
        @Param("workShift") String workShift,
        Pageable pageable
    );

    Optional<StaffProfile> findByDriver_DriverId(Long driverId);

    @Query("SELECT COUNT(sp) FROM StaffProfile sp WHERE sp.station.stationId = :stationId AND sp.active = true")
    long countActiveStaffByStation(@Param("stationId") Long stationId);

    List<StaffProfile> findByStation_StationIdAndActiveTrue(Long stationId);
}
