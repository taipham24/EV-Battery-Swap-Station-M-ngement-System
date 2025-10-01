package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.service.EmailService;
import group8.EVBatterySwapStation_BackEnd.service.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailImpl implements EmailService {
    @Autowired
    private final JavaMailSender mailSender;
    @Autowired
    private EmailTemplateService emailTemplateService;

    @Override
    public void sendBookingConfirmation(String to, Booking booking) {
        try {
            String htmlContent = emailTemplateService.loadBookingTemplate(
                    "templates/booking-confirmation.html",
                    booking.getDriver().getFullName(),
                    booking.getStation().getName(),
                    booking.getStation().getAddress(),
                    booking.getBookingTime().toString(),
                    booking.getStatus().toString()
            );
            // Tạo MimeMessage để gửi HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Xác nhận đặt lịch đổi pin");
            helper.setText(htmlContent, true); // true để gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email", e);
        }
    }
}
