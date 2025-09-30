package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batteries")
@RequiredArgsConstructor
public class BatteryController {
    private final BatteryService batteryService;

    @PostMapping("/station/{stationId}")
    public ResponseEntity<Battery> addBatteryToStation(
            @PathVariable Long stationId,
            @RequestParam BatteryStatus status
    ) {
        return ResponseEntity.ok(batteryService.addBatteryToStation(stationId, status));
    }
}
