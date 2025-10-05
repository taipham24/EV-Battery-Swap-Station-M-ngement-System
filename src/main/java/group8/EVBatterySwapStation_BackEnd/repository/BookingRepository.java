package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Booking;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByDriver(Driver driver);

    boolean existsByDriverAndStationAndBookingTimeBetweenAndStatus(
            Driver driver,
            Station station,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BookingStatus status
    );
    List<Booking> findByStatusAndBookingTimeBefore(BookingStatus status, LocalDateTime time);

}
