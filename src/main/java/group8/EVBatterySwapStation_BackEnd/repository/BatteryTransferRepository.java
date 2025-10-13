package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.BatteryTransfer;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatteryTransferRepository extends JpaRepository<BatteryTransfer, Long> {
    Page<BatteryTransfer> findByStatus(TransferStatus status, Pageable pageable);
    
    Page<BatteryTransfer> findByFromStationOrToStation(Station fromStation, Station toStation, Pageable pageable);
    
    Page<BatteryTransfer> findByFromStationOrToStationAndStatus(Station fromStation, Station toStation, TransferStatus status, Pageable pageable);
    
    Page<BatteryTransfer> findByFromStation(Station fromStation, Pageable pageable);
    
    Page<BatteryTransfer> findByToStation(Station toStation, Pageable pageable);
}
