package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SupportTicketRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SupportTicketResponse;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;

import java.util.List;
import java.util.Map;

public interface SupportTicketService {

    SupportTicketResponse createTicket(Long driverId, SupportTicketRequest request);

    List<SupportTicketResponse> getDriverTickets(Long driverId);

    SupportTicket resolveTicket(Long ticketId);

    Map<String, Long> getTicketStatsByCategory();
}
