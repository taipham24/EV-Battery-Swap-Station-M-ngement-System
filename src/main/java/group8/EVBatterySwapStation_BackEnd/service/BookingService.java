package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingResponse;

import java.time.LocalDateTime;

public interface BookingService {
    BookingResponse createBooking(Long stationId, LocalDateTime bookingTime);
}
