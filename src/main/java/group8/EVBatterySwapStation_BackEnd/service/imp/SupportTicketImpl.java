package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SupportTicketRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.enums.TicketStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SupportTicketRepository;
import group8.EVBatterySwapStation_BackEnd.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportTicketImpl implements SupportTicketService {
    @Autowired
    private final SupportTicketRepository repository;
    @Autowired
    private final DriverRepository driverRepository;
    @Autowired
    private final StationRepository stationRepository;

    @Override
    public SupportTicket createTicket(Long driverId, SupportTicketRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
        if (request.getStationId() != null) {
            station = stationRepository.findById(request.getStationId())
                    .orElse(null);
        }
        SupportTicket ticket = new SupportTicket();
        ticket.setDriver(driver);
        ticket.setStation(station);
        ticket.setIssueType(request.getIssueType());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        repository.save(ticket);

        return ticket;
    }

    @Override
    public List<SupportTicket> getDriverTickets(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        return repository.findByDriver_DriverId(driver.getDriverId());
    }

    @Override
    public SupportTicket resolveTicket(Long ticketId) {
        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPORT_TICKET_NOT_FOUND));
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());
        repository.save(ticket);
        return ticket;
    }
}
