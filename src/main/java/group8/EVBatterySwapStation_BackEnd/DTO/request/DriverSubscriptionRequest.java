package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Data;

@Data
public class DriverSubscriptionRequest {
    private Long driverId;
    private Long planId;
    private Long batteryId;
    private boolean autoRenew; // tự động gia hạn hay không
}
