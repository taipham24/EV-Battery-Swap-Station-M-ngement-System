package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.GlobalPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalPricingRepository extends JpaRepository<GlobalPricing, Long> {
    Optional<GlobalPricing> findTopByOrderByIdDesc();
}
