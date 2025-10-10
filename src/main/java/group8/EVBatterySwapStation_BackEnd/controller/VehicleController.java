package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.VehicleRegistrationRequest;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.Vehicle;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.service.VehicleService;
import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class VehicleController {
    @Autowired
    private final VehicleService vehicleService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private BatteryRepository batteryRepository;

    @PostMapping("/register")
    public ResponseEntity<Vehicle> registerVehicle(@RequestBody VehicleRegistrationRequest request) {
        Driver driver = driverRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        if (!driver.isSubscribed()) {
            throw new IllegalStateException("Driver chưa đăng ký dịch vụ đổi pin");
        }
        Battery battery = batteryRepository.findById(request.getBatteryId())
                .orElseThrow(() -> new RuntimeException("Battery not found with ID: " + request.getBatteryId()));
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(request.getVin());
        vehicle.setBatteryType(request.getBatteryType());
        vehicle.setModel(request.getModel());
        vehicle.setManufacturer(request.getManufacturer());
        vehicle.setImageUrl(request.getImageUrl());
        vehicle.setBattery(battery); // Gán pin hiện tại cho xe
        vehicle.setDriver(driver);

        Vehicle registeredVehicle = vehicleService.registerVehicle(vehicle);
        return new ResponseEntity<>(registeredVehicle, HttpStatus.CREATED);
    }

}
