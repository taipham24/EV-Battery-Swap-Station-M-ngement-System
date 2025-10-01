package group8.EVBatterySwapStation_BackEnd.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Service
public class EmailTemplateService {
    public String loadBookingTemplate(String path, String driverName,
                                      String stationName, String stationAddress,
                                      String bookingTime, String status) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                throw new RuntimeException("Template not found: " + path);
            }

            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            return template.replace("{{driverName}}", driverName)
                    .replace("{{stationName}}", stationName)
                    .replace("{{stationAddress}}", stationAddress)
                    .replace("{{bookingTime}}", bookingTime)
                    .replace("{{status}}", status);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template", e);
        }
    }
}
