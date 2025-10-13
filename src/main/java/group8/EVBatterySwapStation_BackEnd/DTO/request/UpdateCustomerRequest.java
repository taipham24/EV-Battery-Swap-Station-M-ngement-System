package group8.EVBatterySwapStation_BackEnd.DTO.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCustomerRequest {
    private String userName;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Boolean status;
    private Boolean suspended;
}
