package group8.EVBatterySwapStation_BackEnd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group8.EVBatterySwapStation_BackEnd.DTO.request.VehicleRegistrationRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Vehicle;
import group8.EVBatterySwapStation_BackEnd.repository.BatteryRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Vehicle> registerVehicle(
            @ModelAttribute VehicleRegistrationRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        VehicleRegistrationRequest vehicleRequest = objectMapper.convertValue(request, VehicleRegistrationRequest.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.registerVehicle(vehicleRequest, file));
    }

}
