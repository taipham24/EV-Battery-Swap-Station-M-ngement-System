package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByUserName(String userName);

    Optional<Driver> findByUserName(String userName);

    boolean existsByEmail(String email);


}
