package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.VehicleRegistrationRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Vehicle;
import org.springframework.web.multipart.MultipartFile;

public interface VehicleService {

    Vehicle registerVehicle(VehicleRegistrationRequest request, MultipartFile file);
}
