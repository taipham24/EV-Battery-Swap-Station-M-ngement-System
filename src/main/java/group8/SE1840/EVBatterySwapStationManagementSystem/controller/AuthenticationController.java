package group8.SE1840.EVBatterySwapStationManagementSystem.controller;

import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.request.AuthenticationRequest;
import group8.SE1840.EVBatterySwapStationManagementSystem.DTO.response.AuthenticationResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.entity.ApiResponse;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.DriverRepository;
import group8.SE1840.EVBatterySwapStationManagementSystem.repository.RoleRepository;
import group8.SE1840.EVBatterySwapStationManagementSystem.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;
    private final DriverRepository driverRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAuthenticated(result.isAuthenticated());
        authenticationResponse.setToken(result.getToken());
        authenticationResponse.setRefreshToken(result.getRefreshToken());

        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(authenticationResponse);

        return apiResponse;
    }
}
