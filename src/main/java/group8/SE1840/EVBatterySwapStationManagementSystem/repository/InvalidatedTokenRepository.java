package group8.SE1840.EVBatterySwapStationManagementSystem.repository;

import group8.SE1840.EVBatterySwapStationManagementSystem.entity.InvalidatedToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM InvalidatedToken t WHERE t.timeExpired < :currentTime")
    void deleteExpiredTokens(Date currentTime);
}
