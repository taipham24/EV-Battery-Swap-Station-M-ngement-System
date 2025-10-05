package group8.EVBatterySwapStation_BackEnd.configuration;

import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.enums.BookingStatus;
import group8.EVBatterySwapStation_BackEnd.repository.BookingRepository;
import group8.EVBatterySwapStation_BackEnd.service.BookingEventProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingAutoCancelScheduler {
    private final BookingRepository bookingRepository;
    private final BookingEventProducerService bookingEventProducerService;

    @Scheduled(fixedRate = 600000) // 10 phút
    public void autoCancelLateBookings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thresholdTime = now.minusMinutes(30); // 30 phút trước thời điểm hiện tại
        List<Booking> lateBookings = bookingRepository.findByStatusAndBookingTimeBefore(
                group8.EVBatterySwapStation_BackEnd.enums.BookingStatus.PENDING, thresholdTime);
        for (Booking booking : lateBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            bookingEventProducerService.sendBookingAutoCancelled(
                    booking.getBookingId(),
                    booking.getDriver().getEmail(),
                    booking.getStation().getName()
            );
        }
    }
}
