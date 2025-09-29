package group8.EVBatterySwapStation_BackEnd.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code = 1010;
    private String message;
    private T result;

    public ApiResponse(String success, String driverAssignedToVehicleSuccessfully, Vehicle vehicle) {
    }

    public static <T> ApiResponse<T> success(T result) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(1010);
        response.setMessage("Success");
        response.setResult(result);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
