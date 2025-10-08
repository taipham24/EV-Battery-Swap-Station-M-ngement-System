package group8.EVBatterySwapStation_BackEnd.DTO.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubscriptionPlanRequest {
    @NotBlank(message = "Package name is required")
    @Size(max = 100, message = "Package name must not exceed 100 characters")
    private String name;

    private String description;

    @Min(value = 0, message = "Price must be non-negative")
    private double price;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    private Integer swapLimit; // null = không giới hạn
}
