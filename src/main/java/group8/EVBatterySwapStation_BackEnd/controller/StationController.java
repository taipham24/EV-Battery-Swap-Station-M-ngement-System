package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.StationRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PostMapping("/stations")
    public ResponseEntity<Station> createStation(@RequestBody StationRequest request) {
        return ResponseEntity.ok(stationService.createStation(request));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<StationInfoResponse>> getNearbyStations(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") double radiusKm) {
        return ResponseEntity.ok(
                stationService.findNearestStations(lat, lon, radiusKm));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Station>> searchStations(@RequestParam String keyword) {
        return ResponseEntity.ok(stationService.searchStations(keyword));
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationId) {
        stationService.deleteStation(stationId);
        return ResponseEntity.noContent().build();
    }

}
