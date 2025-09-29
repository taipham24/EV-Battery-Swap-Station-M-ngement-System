package group8.EVBatterySwapStation_BackEnd.controller;


import group8.EVBatterySwapStation_BackEnd.DTO.request.AuthenticationRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.AuthenticationResponse;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import group8.EVBatterySwapStation_BackEnd.service.AuthenticationService;
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
