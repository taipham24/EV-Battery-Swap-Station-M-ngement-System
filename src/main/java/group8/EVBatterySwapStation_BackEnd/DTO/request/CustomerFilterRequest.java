package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerFilterRequest {
    private String keyword; // search in userName, email, fullName
    private Boolean status;
    private Boolean suspended;
    private Boolean deleted;
    private Boolean hasActiveSubscription;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
    private int page = 0;
    private int size = 20;
}
