package group8.EVBatterySwapStation_BackEnd.service.imp;


import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.DriverResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.RoleDetail;
import group8.EVBatterySwapStation_BackEnd.enums.Role;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.mapper.DriverMapper;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import group8.EVBatterySwapStation_BackEnd.repository.VehicleRepository;
import group8.EVBatterySwapStation_BackEnd.service.DriverService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverImpl implements DriverService {
    private DriverRepository driverRepository;
    private RoleRepository roleRepository;
    private VehicleRepository vehicleRepository;

    @Override
    public DriverResponse register(DriverDTO driverRequest, String userRoleChoice) {
        if (driverRepository.existsByUserName(driverRequest.getUserName()) || driverRepository.existsByEmail(driverRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        var driver = DriverMapper.mapToDriver(driverRequest);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        driver.setPassword(passwordEncoder.encode(driverRequest.getPassword()));
        HashSet<RoleDetail> roles = new HashSet<>();
        if (userRoleChoice.equalsIgnoreCase("STAFF")) {
            RoleDetail userRole = roleRepository.findByRoleType(Role.STAFF.name()).orElseGet(() -> {
                RoleDetail newRole = new RoleDetail();
                newRole.setRoleType(Role.STAFF.name());
                return roleRepository.save(newRole);
            });
            roles.add(userRole);
            driver.setRoles(roles);
        } else {
            RoleDetail userRole = roleRepository.findByRoleType(Role.DRIVER.name()).orElseGet(() -> {
                RoleDetail newRole = new RoleDetail();
                newRole.setRoleType(Role.DRIVER.name());
                return roleRepository.save(newRole);
            });
            roles.add(userRole);
            driver.setRoles(roles);
        }
        var savedDriver = driverRepository.save(driver);
        return DriverMapper.mapToDriverResponse(savedDriver);
    }


    @Override
    public List<DriverResponse> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream().map(DriverMapper::mapToDriverResponse).toList();
    }

    @Override
    public DriverResponse getUser(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        return DriverMapper.mapToDriverResponse(driver);
    }

    @Override
    public DriverResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String principal = context.getAuthentication().getName();
        Driver driver = null;
        if (principal.matches("\\d+")) {
            Long driverId = Long.valueOf(principal);
            driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        }
        return DriverMapper.mapToDriverResponse(driver);
    }

    @Override
    public DriverResponse updateDriver(Long driverId, DriverDTO newInfoDriver) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_EXISTED));
        driver.setUserName(newInfoDriver.getUserName());
        driver.setEmail(newInfoDriver.getEmail());
        driver.setFullName(newInfoDriver.getFullName());
        return DriverMapper.mapToDriverResponse(driverRepository.save(driver));
    }
    @Override
    public void registerBatterySwapService(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver không tồn tại"));

        driver.setSubscribed(true); // cho phép sử dụng dịch vụ
        driverRepository.save(driver);
    }
}
