package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DriverSubscriptionRequest;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.service.DriverSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver-subscriptions")
@RequiredArgsConstructor
public class DriverSubscriptionController {
    private final DriverSubscriptionService service;

    @PostMapping("/create")
    public ResponseEntity<DriverSubscription> create(@RequestBody DriverSubscriptionRequest request) {
        return ResponseEntity.ok(service.createSubscription(request));
    }

    @GetMapping("/{driverId}/active")
    public ResponseEntity<DriverSubscription> getActive(@PathVariable Long driverId) {
        return ResponseEntity.ok(service.getActiveSubscriptionForDriver(driverId));
    }
}
