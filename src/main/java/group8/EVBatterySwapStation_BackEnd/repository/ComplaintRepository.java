package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Complaint;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {
    Page<Complaint> findByDriverAndStatus(Driver driver, ComplaintStatus status, Pageable pageable);
    
    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);
    
    Page<Complaint> findByType(ComplaintType type, Pageable pageable);
    
    Page<Complaint> findByStatusAndType(ComplaintStatus status, ComplaintType type, Pageable pageable);
    
    long countByStatus(ComplaintStatus status);
    
    long countByType(ComplaintType type);
    
    @Query("SELECT c FROM Complaint c WHERE c.driver.driverId = :driverId")
    Page<Complaint> findByDriverId(@Param("driverId") Long driverId, Pageable pageable);
}
