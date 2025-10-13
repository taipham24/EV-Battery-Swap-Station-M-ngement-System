package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.UpdateStatusRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryHealthMetrics;
import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.service.BatteryAnalyticsService;
import group8.EVBatterySwapStation_BackEnd.service.BatteryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batteries")
@RequiredArgsConstructor
public class BatteryController {
    private final BatteryService batteryService;
    private final BatteryAnalyticsService batteryAnalyticsService;

    @PostMapping("/station/{stationId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Battery> addBatteryToStation(
            @PathVariable Long stationId,
            @RequestParam BatteryStatus status
    ) {
        return ResponseEntity.ok(batteryService.addBatteryToStation(stationId, status));
    }

    @GetMapping("/station/{stationId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Battery>> getBatteriesByStation(
            @PathVariable Long stationId,
            @RequestParam(required = false) BatteryStatus status
    ) {
        return ResponseEntity.ok(batteryService.getBatteriesByStation(stationId, status));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<Battery>> listBatteries(
            @RequestParam(required = false) BatteryStatus status,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Integer capacityMin,
            @RequestParam(required = false) Integer capacityMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "serialNumber,asc") String sort
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(direction, sortParts[0]));
        Page<Battery> result = batteryService.listBatteries(stationId, status, model, capacityMin, capacityMax, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String capacityBuckets
    ) {
        List<int[]> buckets = BatterySummaryBucketParser.parse(capacityBuckets);
        return ResponseEntity.ok(batteryService.summary(stationId, buckets));
    }

    @GetMapping("/{id}/health")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BatteryHealthMetrics> getBatteryHealth(@PathVariable Long id) {
        return ResponseEntity.ok(batteryAnalyticsService.calculateBatteryHealth(id));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<BatteryHealthMetrics.UsageEvent>> getBatteryUsageHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(batteryAnalyticsService.getBatteryUsageHistory(id, pageable));
    }

    @GetMapping("/{id}/inspections")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<BatteryHealthMetrics.InspectionSummary>> getBatteryInspectionHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(batteryAnalyticsService.getBatteryInspectionHistory(id, pageable));
    }

    @GetMapping("/health-report")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemWideBatteryHealthReport() {
        return ResponseEntity.ok(batteryAnalyticsService.getSystemWideBatteryHealthReport());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Battery> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        Battery updated = batteryService.updateStatus(id, request.getStatus(), request.getReason(), request.isAdminOverride());
        return ResponseEntity.ok(updated);
    }

}

class BatterySummaryBucketParser {
    static List<int[]> parse(String input) {
        // simple parser: "0-2000,2001-5000"
        java.util.ArrayList<int[]> list = new java.util.ArrayList<>();
        if (input == null || input.isBlank()) return list;
        for (String part : input.split(",")) {
            String[] s = part.trim().split("-");
            if (s.length != 2) throw new IllegalArgumentException("INVALID_BUCKETS");
            int start = Integer.parseInt(s[0]);
            int end = Integer.parseInt(s[1]);
            if (start > end) throw new IllegalArgumentException("INVALID_BUCKETS");
            list.add(new int[]{start, end});
        }
        return list;
    }
}


