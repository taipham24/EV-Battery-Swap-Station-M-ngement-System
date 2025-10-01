package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingResponse;
import group8.EVBatterySwapStation_BackEnd.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
}
