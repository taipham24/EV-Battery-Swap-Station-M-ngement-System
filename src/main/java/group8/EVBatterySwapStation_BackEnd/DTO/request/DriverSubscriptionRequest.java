package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Data;

@Data
public class DriverSubscriptionRequest {
    private Long planId;
    private boolean autoRenew; // tự động gia hạn hay không
}
