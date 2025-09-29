package group8.EVBatterySwapStation_BackEnd.repository;


import group8.EVBatterySwapStation_BackEnd.entity.RoleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleDetail, Long> {
    Optional<RoleDetail> findByRoleType(String roleType);
}
