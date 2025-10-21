package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SupportTicket;
import group8.EVBatterySwapStation_BackEnd.service.EmailService;
import group8.EVBatterySwapStation_BackEnd.service.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
                    "/booking-confirmation.html",
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

    @Override
    public void sendBookingRejected(String to, Booking booking) {
        try {
            String htmlContent = emailTemplateService.loadBookingTemplate(
                    "/booking-rejected.html",
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
            helper.setSubject("Thông báo từ chối đặt lịch đổi pin");
            helper.setText(htmlContent, true); // true để gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking rejection email", e);
        }
    }

    @Override
    public void sendRenewalSuccessEmail(String to, DriverSubscription sub) {
        try {
            String htmlContent = emailTemplateService.loadSubscriptionTemplate(
                    "/renewal-success.html",
                    sub.getDriver().getFullName(),
                    sub.getPlan().getName(),
                    sub.getStartDate().toString(),
                    sub.getEndDate().toString(),
                    String.valueOf(sub.getPlan().getPrice())
            );
            // Tạo MimeMessage để gửi HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Thông báo gia hạn gói đăng ký thành công");
            helper.setText(htmlContent, true); // true để gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking rejection email", e);
        }
    }

    @Override
    public void sendEscalationNotice(SupportTicket ticket) {
        String subject = "⚠️ Ticket #" + ticket.getTicketId() + " bị quá hạn SLA!";
        String content = """
            Ticket %d (Category: %s) đã quá hạn xử lý.
            Ưu tiên: %s
            Trạng thái hiện tại: %s
            """.formatted(ticket.getTicketId(), ticket.getCategory(), ticket.getPriority(), ticket.getStatus());
        // Gửi mail tới admin
        // mailSender.send(...)
    }
}
