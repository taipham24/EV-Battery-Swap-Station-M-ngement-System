package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingResponse;
import group8.EVBatterySwapStation_BackEnd.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/{stationId}/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookingTime) {
        BookingResponse response = bookingService.createBooking(stationId, bookingTime);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingId}/reschedule")
    public ResponseEntity<BookingResponse> rescheduleBooking(
            @PathVariable Long bookingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newBookingTime) {
        BookingResponse response = bookingService.rescheduleBooking(bookingId, newBookingTime);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long bookingId, @PathVariable Long staffId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId, staffId));
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> rejectBooking(@PathVariable Long bookingId, @PathVariable Long staffId) {
        return ResponseEntity.ok(bookingService.rejectBooking(bookingId, staffId));
    }
}
