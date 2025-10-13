package group8.EVBatterySwapStation_BackEnd.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryTransferRequest {
    @NotNull(message = "Battery ID is required")
    private Long batteryId;
    
    @NotNull(message = "Destination station ID is required")
    private Long toStationId;
    
    @NotBlank(message = "Transfer reason is required")
    @Size(max = 512, message = "Reason must not exceed 512 characters")
    private String reason;
    
    @Size(max = 512, message = "Notes must not exceed 512 characters")
    private String notes;
}
