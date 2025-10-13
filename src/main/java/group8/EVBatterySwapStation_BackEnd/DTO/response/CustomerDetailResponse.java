package group8.EVBatterySwapStation_BackEnd.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailResponse {
    private Long driverId;
    private String userName;
    private String email;
    private String fullName;
    private boolean status;
    private boolean suspended;
    private String suspensionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    
    // Subscription summary
    private SubscriptionSummary subscriptionSummary;
    
    // Statistics
    private Long totalSwaps;
    private Long totalRevenue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionSummary {
        private Long subscriptionId;
        private String planName;
        private String batterySerial;
        private int swapsUsed;
        private int swapLimit;
        private int swapsRemaining;
        private boolean active;
        private boolean hasActiveSubscription;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private boolean autoRenew;
    }
}
