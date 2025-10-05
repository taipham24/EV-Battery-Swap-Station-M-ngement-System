package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingEmailMessage;
import group8.EVBatterySwapStation_BackEnd.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventProducerService {
    private final KafkaTemplate<String, BookingEmailMessage> kafkaTemplate;
    private static final String TOPIC = "booking-email-topic";

    public void sendBookingConfirmed(Long bookingId, String toEmail, String stationName, LocalDateTime bookingTime) {
        BookingEmailMessage message = BookingEmailMessage.builder()
                .bookingId(bookingId)
                .status(BookingStatus.CONFIRMED)
                .to(toEmail)
                .subject("✅ Xác nhận lịch đổi pin thành công")
                .body("Bạn đã đặt lịch đổi pin thành công tại trạm **" + stationName + "** vào lúc **" + bookingTime + "**.")
                .build();
        send(message);
    }

    // Gửi thông báo hủy booking (do staff từ chối)
    public void sendBookingRejected(Long bookingId, String toEmail, String stationName) {
        BookingEmailMessage message = BookingEmailMessage.builder()
                .bookingId(bookingId)
                .status(BookingStatus.CANCELLED)
                .to(toEmail)
                .subject("❌ Lịch đổi pin của bạn đã bị hủy")
                .body("Lịch đổi pin tại trạm **" + stationName + "** đã bị từ chối hoặc hủy bởi nhân viên.")
                .build();
        send(message);
    }

    // Gửi thông báo đổi lịch
    public void sendBookingRescheduled(Long bookingId, String toEmail, String stationName, String newTime) {
        BookingEmailMessage message = BookingEmailMessage.builder()
                .bookingId(bookingId)
                .status(BookingStatus.RESCHEDULED)
                .to(toEmail)
                .subject("🔄 Cập nhật lịch đổi pin")
                .body("Lịch đổi pin của bạn tại trạm **" + stationName + "** đã được đổi sang **" + newTime + "**.")
                .build();
        send(message);
    }

    // Gửi thông báo auto hủy (không đến đúng hẹn)
    public void sendBookingAutoCancelled(Long bookingId, String toEmail, String stationName) {
        BookingEmailMessage message = BookingEmailMessage.builder()
                .bookingId(bookingId)
                .status(BookingStatus.CANCELLED)
                .to(toEmail)
                .subject("⚠️ Lịch đổi pin đã bị hủy do không đến đúng giờ")
                .body("Lịch đổi pin tại trạm **" + stationName + "** đã bị tự động hủy do bạn không đến đúng hẹn.")
                .build();
        send(message);
    }

    private void send(BookingEmailMessage message) {
        try {
            kafkaTemplate.send(TOPIC, message);
            log.info("📩 Sent booking email event to Kafka: {}", message);
        } catch (Exception e) {
            log.error("❌ Failed to send booking event to Kafka", e);
        }
    }
}
