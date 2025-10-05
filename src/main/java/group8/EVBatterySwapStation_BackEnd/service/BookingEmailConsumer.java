package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingEmailMessage;
import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.enums.BookingStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEmailConsumer {
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "booking-email-topic", groupId = "booking-email-group")
    public void consume(BookingEmailMessage message) {
        Booking booking = bookingRepository.findById(message.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
        if (BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            emailService.sendBookingConfirmation(booking.getDriver().getEmail(), booking);
        } else if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            emailService.sendBookingRejected(booking.getDriver().getEmail(), booking);
        }
    }
}
