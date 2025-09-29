package group8.EVBatterySwapStation_BackEnd.mapper;


import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.DriverResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.RoleResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DriverMapper {
    @Builder
    public static DriverDTO mapToDriverDto(Driver driver) {
        return new DriverDTO(
                driver.getUserName(),
                driver.getPassword(),
                driver.getEmail(),
                driver.getFullName(),
                driver.isStatus()
        );
    }

    public static Driver mapToDriver(DriverDTO driverDto) {
        Driver driver = new Driver();
        driver.setUserName(driverDto.getUserName());
        driver.setPassword(driverDto.getPassword());
        driver.setEmail(driverDto.getEmail());
        driver.setFullName(driverDto.getFullName());
        driver.setStatus(driverDto.isStatus());
        return driver;
    }

    public static DriverResponse mapToDriverResponse(Driver driver) {
        Set<RoleResponse> role = (driver.getRoles() != null)
                ? driver.getRoles().stream()
                .map(roleDetail -> new RoleResponse(roleDetail.getRoleType()))
                .collect(Collectors.toSet())
                : new HashSet<>();
        return new DriverResponse(
                driver.getDriverId(),
                driver.getUserName(),
                driver.getEmail(),
                driver.getFullName(),
                driver.isStatus(),
                role
        );
    }
}
