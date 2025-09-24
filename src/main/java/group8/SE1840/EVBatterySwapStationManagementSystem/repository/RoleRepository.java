package group8.SE1840.EVBatterySwapStationManagementSystem.repository;

import group8.SE1840.EVBatterySwapStationManagementSystem.entity.RoleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleDetail, Long> {
    Optional<RoleDetail> findByRoleType(String roleType);
}
