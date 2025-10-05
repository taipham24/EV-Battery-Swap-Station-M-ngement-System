package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingResponse;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

public interface BookingService {
    BookingResponse createBooking(Long stationId, LocalDateTime bookingTime);

    @Transactional
    BookingResponse confirmBooking(Long bookingId, Long staffId);

    @Transactional
    BookingResponse rejectBooking(Long bookingId, Long staffId);

    @Transactional
    BookingResponse rescheduleBooking(Long bookingId, LocalDateTime newTime);
}
