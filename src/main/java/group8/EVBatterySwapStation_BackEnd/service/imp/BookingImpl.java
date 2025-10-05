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
import group8.EVBatterySwapStation_BackEnd.service.BookingEventProducerService;
import group8.EVBatterySwapStation_BackEnd.service.BookingService;
import group8.EVBatterySwapStation_BackEnd.service.EmailService;
import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    @Autowired
    private BookingEventProducerService bookingEventProducerService;

    @Override
    public BookingResponse createBooking(Long stationId, LocalDateTime bookingTime) {
        Driver driver = driverRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new AppException(ErrorCode.STATION_NOT_EXISTED));
        LocalDate date = bookingTime.toLocalDate();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        boolean alreadyBooked = bookingRepository.existsByDriverAndStationAndBookingTimeBetweenAndStatus(
                driver, station, startOfDay, endOfDay, BookingStatus.PENDING
        );

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

    @Transactional
    @Override
    public BookingResponse confirmBooking(Long bookingId, Long staffId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        bookingEventProducerService.sendBookingConfirmed(bookingId,
                booking.getDriver().getEmail(),
                booking.getStation().getName(),
                booking.getBookingTime());
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .driverId(booking.getDriver().getDriverId())
                .stationId(booking.getStation().getStationId())
                .reservedBatteryId(booking.getReservedBattery().getBatteryId())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .confirmed(true)
                .build();
    }

    @Transactional
    @Override
    public BookingResponse rejectBooking(Long bookingId, Long staffId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Battery battery = booking.getReservedBattery();
        if (battery != null) {
            battery.setStatus(BatteryStatus.FULL);
            batteryRepository.save(battery);
        }
        bookingRepository.save(booking);
        bookingEventProducerService.sendBookingRejected(bookingId,
                booking.getDriver().getEmail(),
                booking.getStation().getName());
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .driverId(booking.getDriver().getDriverId())
                .stationId(booking.getStation().getStationId())
                .reservedBatteryId(booking.getReservedBattery().getBatteryId())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .confirmed(false)
                .build();
    }

    @Transactional
    @Override
    public BookingResponse rescheduleBooking(Long bookingId, LocalDateTime newTime) {
        Driver driver = driverRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
        if (!booking.getDriver().getDriverId().equals(driver.getDriverId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.BOOKING_CANNOT_RESCHEDULE);
        }
        booking.setBookingTime(newTime);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);
        bookingEventProducerService.sendBookingRescheduled(bookingId,
                booking.getDriver().getEmail(),
                booking.getStation().getName(),
                newTime.toString());
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .driverId(booking.getDriver().getDriverId())
                .stationId(booking.getStation().getStationId())
                .reservedBatteryId(booking.getReservedBattery().getBatteryId())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .confirmed(false)
                .build();

    }
}
