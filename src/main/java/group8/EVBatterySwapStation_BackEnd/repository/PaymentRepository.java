package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Payment;
import group8.EVBatterySwapStation_BackEnd.entity.SwapTransaction;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstBySwapAndStatus(SwapTransaction swap, PaymentStatus status);
}



