package group8.EVBatterySwapStation_BackEnd.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(1000, "Invalid message key", HttpStatusCode.valueOf(400)),
    UNCATAGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User existed (please check your username or your email)", HttpStatusCode.valueOf(400)),
    INVALID_DRIVERID(1003, "Invalid username", HttpStatusCode.valueOf(400)),
    DRIVER_NOT_EXISTED(1005, "User invalid (please check your username or your password)", HttpStatusCode.valueOf(404)),
    UNAUTHENDICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "Unauthorized", HttpStatus.UNAUTHORIZED),
    INVALID_ROLE(1002, "Invalid role", HttpStatusCode.valueOf(400)),
    PAYMENT_ERROR(1004, "Payment error", HttpStatusCode.valueOf(400)),
    VEHICLE_NOT_EXISTED(1006, "Vehicle existed (please check your license plate)", HttpStatusCode.valueOf(400)),
    VEHICLE_ALREADY_REGISTERED(1009, "Vehicle already registered", HttpStatusCode.valueOf(400)),
    VEHICLE_INELIGIBLE_FOR_SERVICE(1010, "Vehicle ineligible for service (vehicle is too old)", HttpStatusCode.valueOf(400)),
    VIN_ALREADY_EXISTS(1011, "Vehicle with this VIN already exists", HttpStatusCode.valueOf(400)),
    DRIVER_ALREADY_HAS_VEHICLE(1012, "Driver already has a registered vehicle", HttpStatusCode.valueOf(400)),
    STATION_NOT_EXISTED(2001, "Station not existed", HttpStatusCode.valueOf(404)),
    BATTERY_NOT_EXISTED(3001, "Battery not existed", HttpStatusCode.valueOf(404)),
    STATION_FULL(2002, "Station is full", HttpStatusCode.valueOf(400)),
    BOOKING_ALREADY_EXISTED(4001, "Booking already existed", HttpStatusCode.valueOf(400)),
    BOOKING_NOT_EXISTED(4002, "Booking not existed", HttpStatusCode.valueOf(404)),
    BOOKING_NO_BATTERY_AVAILABLE(4003, "No battery available at the station", HttpStatusCode.valueOf(400)),
    INVALID_BOOKING_STATUS(4004, "Invalid booking status", HttpStatusCode.valueOf(400)),
    BOOKING_CANNOT_RESCHEDULE(4005, "Booking cannot be rescheduled", HttpStatusCode.valueOf(400)),
    BATTERY_NOT_FOUND(5000, "Battery not found", HttpStatusCode.valueOf(404)),
    INVALID_QUERY(5001, "Invalid query parameters", HttpStatusCode.valueOf(400)),
    INVALID_TRANSITION(5002, "Invalid status transition", HttpStatusCode.valueOf(409)),
    SERIAL_ALREADY_EXISTS(5003, "Serial number already exists", HttpStatusCode.valueOf(409)),
    INVALID_BUCKETS(5004, "Invalid capacity buckets format", HttpStatusCode.valueOf(400)),
    SUBSCRIPTION_NOT_FOUND(1013, "Subscription not found", HttpStatusCode.valueOf(404)),
    SUBSCRIPTION_INACTIVE(1014, "Subscription is inactive", HttpStatusCode.valueOf(400)),
    BOOKING_INVALID(6001, "Booking not active/not found", HttpStatusCode.valueOf(400)),
    BATTERY_NOT_AVAILABLE(6002, "Reserved battery not available", HttpStatusCode.valueOf(409)),
    ALREADY_PAID(6003, "Payment already recorded", HttpStatusCode.valueOf(409)),
    AMOUNT_MISMATCH(6004, "Amount mismatch", HttpStatusCode.valueOf(400)),
    BATTERY_NOT_FOUND(6005, "Battery not found", HttpStatusCode.valueOf(404)),
    ;

    @Getter
    private final int code;
    private String message;
    @Getter
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }
}
