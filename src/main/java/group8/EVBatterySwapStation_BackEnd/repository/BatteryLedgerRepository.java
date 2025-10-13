package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.BatteryLedger;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryLedgerAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatteryLedgerRepository extends JpaRepository<BatteryLedger, Long> {
    long countByBatteryAndAction(Battery battery, BatteryLedgerAction action);
    
    Page<BatteryLedger> findByBatteryOrderByCreatedAtDesc(Battery battery, Pageable pageable);
}




