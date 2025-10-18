package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SupportTicketRequest;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportTicketController {
    private final SupportTicketService service;

    @PostMapping("/create")
    public ResponseEntity<SupportTicket> createTicket(@RequestParam Long driverId, @RequestBody SupportTicketRequest request){
        return ResponseEntity.ok(service.createTicket(driverId, request));
    }
}
