package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SupportTicketRequest;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;

public interface SupportTicketService {
    SupportTicket createTicket(Long driverId, SupportTicketRequest request);
}
