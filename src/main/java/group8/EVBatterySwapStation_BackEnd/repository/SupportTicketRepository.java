package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByDriver_DriverId(Long driverId);

    @Query("SELECT t.category, COUNT(t) FROM SupportTicket t GROUP BY t.category")
    List<Object[]> countTicketsByCategory();

    @Query("SELECT t.station, COUNT(t) FROM SupportTicket t GROUP BY t.station")
    List<Object[]> countTicketsByStation();

    @Query("SELECT t.status, COUNT(t) FROM SupportTicket t GROUP BY t.status")
    List<Object[]> countTicketsByStatus();
}
