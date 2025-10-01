package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.response.BookingResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.enums.BookingStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.BookingRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.service.BookingService;
import group8.EVBatterySwapStation_BackEnd.service.EmailService;
import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingImpl implements BookingService {
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private BatteryRepository batteryRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public BookingResponse createBooking(Long stationId, LocalDateTime bookingTime) {
        Driver driver = driverRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
        boolean alreadyBooked = bookingRepository.existsByDriverAndStationAndStatus(driver, station, "PENDING");
        if (alreadyBooked) {
            throw new AppException(ErrorCode.BOOKING_ALREADY_EXISTED);
        }
        long available = batteryRepository.countByStationAndStatus(station, BatteryStatus.FULL);
        if (available <= 0) {
            throw new AppException(ErrorCode.BOOKING_NO_BATTERY_AVAILABLE);
        }
        Battery reservedBattery = batteryRepository.findFirstByStationAndStatus(station, BatteryStatus.FULL)
                .orElseThrow(() -> new AppException(ErrorCode.BATTERY_NOT_EXISTED));
        reservedBattery.setStatus(BatteryStatus.RESERVED);
        batteryRepository.save(reservedBattery);

        Booking booking = Booking.builder()
                .driver(driver)
                .station(station)
                .reservedBattery(reservedBattery)
                .bookingTime(bookingTime)
                .status(BookingStatus.PENDING)
                .build();
        Booking savedBooking = bookingRepository.save(booking);
        emailService.sendBookingConfirmation(driver.getEmail(), savedBooking);
        return BookingResponse.builder()
                .bookingId(savedBooking.getBookingId())
                .driverId(driver.getDriverId())
                .stationId(station.getStationId())
                .reservedBatteryId(reservedBattery.getBatteryId())
                .bookingTime(savedBooking.getBookingTime())
                .status(savedBooking.getStatus())
                .confirmed(savedBooking.getStatus() == BookingStatus.CONFIRMED)
                .build();
    }
}
