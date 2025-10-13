package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.BatteryInspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatteryInspectionRepository extends JpaRepository<BatteryInspection, Long> {
    Page<BatteryInspection> findByBatteryOrderByInspectedAtDesc(Battery battery, Pageable pageable);
    
    Optional<BatteryInspection> findFirstByBatteryOrderByInspectedAtDesc(Battery battery);
}




