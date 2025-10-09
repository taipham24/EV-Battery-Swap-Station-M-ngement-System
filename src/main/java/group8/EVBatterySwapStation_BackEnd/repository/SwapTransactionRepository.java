package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.SwapTransaction;
import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwapTransactionRepository extends JpaRepository<SwapTransaction, Long> {
    Page<SwapTransaction> findByDriver_DriverId(Long driverId, Pageable pageable);
    Page<SwapTransaction> findByStation_StationId(Long stationId, Pageable pageable);
    Page<SwapTransaction> findByStatus(SwapStatus status, Pageable pageable);
}



