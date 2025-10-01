package group8.EVBatterySwapStation_BackEnd.DTO.response;

import group8.EVBatterySwapStation_BackEnd.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Long bookingId;
    private Long driverId;
    private Long stationId;
    private Long reservedBatteryId; // pin được giữ cho driver
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private boolean confirmed;

}
