package group8.SE1840.EVBatterySwapStationManagementSystem.exception;

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
    VEHICLE_NOT_EXISTED(1006, "Vehicle existed (please check your license plate)", HttpStatusCode.valueOf(400))
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
