package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SupportTicketRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SupportTicketResponse;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportTicketController {
    private final SupportTicketService service;

    @PostMapping("/create")
    public ResponseEntity<SupportTicketResponse> createTicket(@RequestParam Long driverId, @RequestBody SupportTicketRequest request) {
        return ResponseEntity.ok(service.createTicket(driverId, request));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<SupportTicketResponse>> getDriverTickets(@PathVariable Long driverId) {
        return ResponseEntity.ok(service.getDriverTickets(driverId));
    }

    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<SupportTicket> resolveTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(service.resolveTicket(ticketId));
    }

    @GetMapping("/admin/stats/category")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public ResponseEntity<Map<String, Long>> getTicketStatsByCategory() {
        return ResponseEntity.ok(service.getTicketStatsByCategory());
    }

    @GetMapping("/admin/stats/status")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public ResponseEntity<Map<String, Long>> getTicketStatsByStatus() {
        return ResponseEntity.ok(service.getTicketStatsByStation());
    }

}
