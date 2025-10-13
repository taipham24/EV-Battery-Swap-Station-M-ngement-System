package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.RoleDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByUserName(String userName);

    Optional<Driver> findByUserName(String userName);

    boolean existsByEmail(String email);

    // Customer management queries
    Page<Driver> findByDeletedFalseAndRolesContaining(RoleDetail role, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.deleted = false AND " +
           "EXISTS (SELECT r FROM d.roles r WHERE r.roleType = 'DRIVER') AND " +
           "(LOWER(d.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Driver> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Driver d JOIN d.roles r WHERE r.roleType = 'DRIVER' AND d.deleted = false")
    long countActiveCustomers();

    Optional<Driver> findByDriverIdAndDeletedFalse(Long driverId);
}
