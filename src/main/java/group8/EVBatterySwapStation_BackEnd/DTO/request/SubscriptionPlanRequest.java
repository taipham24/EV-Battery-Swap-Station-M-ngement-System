package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Data;

@Data
public class SubscriptionPlanRequest {
    private String name;
    private String description;
    private double price;
    private Integer durationDays;
    private Integer swapLimit; // null = không giới hạn
}
