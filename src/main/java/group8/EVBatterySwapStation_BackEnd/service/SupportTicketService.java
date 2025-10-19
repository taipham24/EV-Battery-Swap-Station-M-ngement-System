package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SupportTicketRequest;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;

import java.util.List;

public interface SupportTicketService {
    SupportTicket createTicket(Long driverId, SupportTicketRequest request);

    List<SupportTicket> getDriverTickets(Long driverId);

    SupportTicket resolveTicket(Long ticketId);
}
