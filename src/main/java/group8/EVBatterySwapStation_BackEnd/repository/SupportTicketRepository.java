package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.enums.SupportCategory;
import group8.EVBatterySwapStation_BackEnd.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByDriver_DriverId(Long driverId);

    List<SupportTicket> findByStatus(TicketStatus status);

    List<SupportTicket> findByCategory(SupportCategory category);

    List<SupportTicket> findByStationId(Long stationId);

    List<SupportTicket> findByAssignedStaffId(Long staffId);

    @Query("SELECT t.category, COUNT(t) FROM SupportTicket t GROUP BY t.category")
    List<Object[]> countTicketsByCategory();

    @Query("SELECT t.station, COUNT(t) FROM SupportTicket t GROUP BY t.station")
    List<Object[]> countTicketsByStation();

    @Query("SELECT t.status, COUNT(t) FROM SupportTicket t GROUP BY t.status")
    List<Object[]> countTicketsByStatus();

    @Query("SELECT t.assignedStaff, COUNT(t) FROM SupportTicket t GROUP BY t.assignedStaff")
    List<Object[]> countTicketsByStaff();

    @Query("SELECT AVG(DATEDIFF(t.resolvedAt, t.createdAt)) FROM SupportTicket t WHERE t.status = 'RESOLVED'")
    Double getAverageResolutionTime();
}
