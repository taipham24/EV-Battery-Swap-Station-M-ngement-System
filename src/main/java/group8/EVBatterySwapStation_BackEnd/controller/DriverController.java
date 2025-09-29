package group8.EVBatterySwapStation_BackEnd.controller;


import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.DriverResponse;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{driverId}/register-swap")
    public ResponseEntity<String> registerSwapService(@PathVariable Long driverId) {
        driverService.registerBatterySwapService(driverId);
        return ResponseEntity.ok("Driver đã đăng ký dịch vụ đổi pin thành công");
    }

}
