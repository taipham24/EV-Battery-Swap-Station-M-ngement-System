package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintResolutionRequest {
    @NotNull(message = "New status is required")
    private ComplaintStatus newStatus;
    
    @NotBlank(message = "Resolution is required")
    @Size(max = 2000, message = "Resolution must not exceed 2000 characters")
    private String resolution;
    
    private Long replacementBatteryId; // Optional - for battery defect cases
}
