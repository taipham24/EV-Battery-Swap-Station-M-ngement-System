package group8.EVBatterySwapStation_BackEnd.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Service
public class EmailTemplateService {
    public String loadBookingTemplate(String fileName, String driverName,
                                      String stationName,
                                      String stationAddress,
                                      String bookingTime,
                                      String status) {

        try {
            ClassPathResource resource = new ClassPathResource("templates/email/" + fileName);
            try (InputStream inputStream = resource.getInputStream()) {
                String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                return template.replace("{{driverName}}", driverName)
                        .replace("{{stationName}}", stationName)
                        .replace("{{stationAddress}}", stationAddress)
                        .replace("{{bookingTime}}", bookingTime)
                        .replace("{{status}}", status);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template", e);
        }
    }

    public String loadSubscriptionTemplate(String fileName, String driverName,
                                           String planName,
                                           String startDate,
                                           String endDate,
                                           String amount) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/" + fileName);
            try (InputStream inputStream = resource.getInputStream()) {
                String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                return template
                        .replace("{{driverName}}", driverName != null ? driverName : "")
                        .replace("{{planName}}", planName != null ? planName : "")
                        .replace("{{startDate}}", startDate != null ? startDate : "")
                        .replace("{{endDate}}", endDate != null ? endDate : "")
                        .replace("{{amount}}", amount != null ? amount : "");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template", e);
        }
    }
}
