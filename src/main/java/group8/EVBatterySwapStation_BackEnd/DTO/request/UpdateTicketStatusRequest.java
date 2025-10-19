package group8.EVBatterySwapStation_BackEnd.DTO.request;

import group8.EVBatterySwapStation_BackEnd.enums.TicketStatus;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {
    private TicketStatus status;
    private String notes;
}
