package group8.SE1840.EVBatterySwapStationManagementSystem.controller;

import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.VehicleRequest;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.VehicleResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.ApiResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.service.VehicleService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping("/register/{driverId}")
    public ApiResponse<VehicleResponse> registerVehicle(@PathVariable Long driverId, @RequestBody VehicleRequest request) {
        ApiResponse<VehicleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(vehicleService.registerVehicle(driverId, request));
        return apiResponse;
    }
}
