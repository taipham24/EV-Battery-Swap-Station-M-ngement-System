package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @GetMapping("/nearby")
    public ResponseEntity<List<StationInfoResponse>> getNearbyStations(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") double radiusKm) {
        return ResponseEntity.ok(
                stationService.findNearestStations(lat, lon, radiusKm));
    }
    
}
