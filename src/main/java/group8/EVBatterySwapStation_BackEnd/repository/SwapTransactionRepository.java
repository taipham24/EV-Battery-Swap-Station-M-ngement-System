package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.SwapTransaction;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SwapTransactionRepository extends JpaRepository<SwapTransaction, Long> {
    Page<SwapTransaction> findByDriver_DriverId(Long driverId, Pageable pageable);
    Page<SwapTransaction> findByStation_StationId(Long stationId, Pageable pageable);
    Page<SwapTransaction> findByStatus(SwapStatus status, Pageable pageable);
    
    long countByStationAndCreatedAtAfter(Station station, LocalDateTime dateTime);
    
    long countByStationAndStatusIn(Station station, List<SwapStatus> statuses);

    // Analytics queries
    @Query("SELECT SUM(s.amountVnd) FROM SwapTransaction s WHERE " +
           "s.paidAt BETWEEN :startDate AND :endDate AND s.status = 'COMPLETED'")
    Long calculateTotalRevenue(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT FUNCTION('DATE', s.paidAt) as date, SUM(s.amountVnd) as revenue " +
           "FROM SwapTransaction s WHERE s.paidAt BETWEEN :startDate AND :endDate " +
           "AND s.status = 'COMPLETED' GROUP BY FUNCTION('DATE', s.paidAt) ORDER BY date")
    List<Object[]> calculateDailyRevenue(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT s.station.stationId, s.station.name, SUM(s.amountVnd) as revenue, COUNT(s) as swapCount " +
           "FROM SwapTransaction s WHERE s.paidAt BETWEEN :startDate AND :endDate " +
           "AND s.status = 'COMPLETED' GROUP BY s.station.stationId, s.station.name ORDER BY revenue DESC")
    List<Object[]> calculateRevenueByStation(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT FUNCTION('DATE', s.createdAt) as date, COUNT(s) as count " +
           "FROM SwapTransaction s WHERE s.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', s.createdAt) ORDER BY date")
    List<Object[]> calculateDailySwapCounts(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT FUNCTION('HOUR', s.createdAt) as hour, COUNT(s) as count, AVG(s.amountVnd) as avgRevenue " +
           "FROM SwapTransaction s WHERE s.createdAt BETWEEN :startDate AND :endDate " +
           "AND s.status IN ('COMPLETED', 'PAID', 'INSPECTED') " +
           "GROUP BY FUNCTION('HOUR', s.createdAt) ORDER BY hour")
    List<Object[]> calculateHourlyPattern(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT FUNCTION('HOUR', s.createdAt) as hour, " +
           "FUNCTION('DAYOFWEEK', s.createdAt) as dayOfWeek, " +
           "COUNT(s) as count " +
           "FROM SwapTransaction s WHERE s.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('HOUR', s.createdAt), FUNCTION('DAYOFWEEK', s.createdAt)")
    List<Object[]> calculateSwapHeatmap(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT s.status, COUNT(s) FROM SwapTransaction s " +
           "WHERE s.createdAt BETWEEN :startDate AND :endDate GROUP BY s.status")
    List<Object[]> calculateSwapsByStatus(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
}




