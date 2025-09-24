package group8.SE1840.EVBatterySwapStationManagementSystem.controller;

import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.DriverDTO;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.DriverResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.ApiResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.Vehicle;
import group8.SE1840.EVBatterySwapStationManagementSystem.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/register")
    public ApiResponse<DriverResponse> register(@RequestBody DriverDTO driverDto) {

        ApiResponse<DriverResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(driverService.register(driverDto, "DRIVER"));

        return apiResponse;
    }

    @GetMapping("/getDrivers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<List<DriverResponse>> getAllDriver() {
        ApiResponse<List<DriverResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(driverService.getAllDrivers());
        return apiResponse;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<DriverResponse> getUser(@PathVariable("id") Long driverId) {
        ApiResponse<DriverResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(driverService.getUser(driverId));
        return apiResponse;
    }

    @GetMapping("/myInfo")
    public ApiResponse<DriverResponse> getMyInfo() {
        ApiResponse<DriverResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(driverService.getMyInfo());
        return apiResponse;
    }

    @PutMapping("/updateDriver/{id}")
    public ApiResponse<DriverResponse> updateDriver(@PathVariable("id") Long driverId, @RequestBody DriverDTO newInfoDriver) {
        ApiResponse<DriverResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(driverService.updateDriver(driverId, newInfoDriver));
        return apiResponse;
    }

}
