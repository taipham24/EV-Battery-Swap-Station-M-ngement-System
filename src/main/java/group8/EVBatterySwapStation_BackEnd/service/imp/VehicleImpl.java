package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.VehicleRegistrationRequest;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.Vehicle;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.VehicleRepository;
import group8.EVBatterySwapStation_BackEnd.service.FirebaseStorageService;
import group8.EVBatterySwapStation_BackEnd.service.VehicleService;
import group8.EVBatterySwapStation_BackEnd.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VehicleImpl implements VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Override
    public Vehicle registerVehicle(VehicleRegistrationRequest request, MultipartFile file) {
        Driver driver = driverRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        if (!driver.isSubscribed()) {
            throw new IllegalStateException("Driver chưa đăng ký dịch vụ đổi pin");
        }
        if (request.getVin() == null || request.getVin().isEmpty()) {
            throw new IllegalArgumentException("VIN is required");
        }
        if (request.getBatteryType() == null) {
            throw new IllegalArgumentException("Battery type is required");
        }
        if (request.getModel() == null || request.getModel().isEmpty()) {
            throw new IllegalArgumentException("Model is required");
        }
        if (request.getManufacturer() == null || request.getManufacturer().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer is required");
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(request.getVin());
        vehicle.setBatteryType(request.getBatteryType());
        vehicle.setModel(request.getModel());
        vehicle.setManufacturer(request.getManufacturer());
        vehicle.setDriver(driver);
        vehicleRepository.save(vehicle);

        if (file != null && !file.isEmpty()) {
            try {
                String imageUrl = firebaseStorageService.uploadFile(file, "vehicleImages/");
                vehicle.setImageUrl(imageUrl);

            } catch (IOException e) {
                e.printStackTrace();
                throw new AppException(ErrorCode.FAIL_UPLOADFILE);
            }
        }
        return vehicle;
    }
}
