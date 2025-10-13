package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Payment;
import group8.EVBatterySwapStation_BackEnd.entity.SwapTransaction;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstBySwapAndStatus(SwapTransaction swap, PaymentStatus status);

    // Analytics queries
    @Query("SELECT p.method, SUM(p.amountVnd), COUNT(p) FROM Payment p " +
           "WHERE p.paidAt BETWEEN :startDate AND :endDate AND p.status = 'SUCCESS' " +
           "GROUP BY p.method")
    List<Object[]> calculateRevenueByPaymentMethod(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @Query("SELECT SUM(p.amountVnd) FROM Payment p WHERE p.paidAt BETWEEN :startDate AND :endDate AND p.status = 'SUCCESS'")
    Long calculateTotalPayments(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
}