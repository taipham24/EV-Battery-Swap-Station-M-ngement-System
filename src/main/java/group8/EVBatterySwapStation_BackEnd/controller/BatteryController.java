package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.entity.Battery;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.service.BatteryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Battery>> getBatteriesByStation(
            @PathVariable Long stationId,
            @RequestParam(required = false) BatteryStatus status
    ) {
        return ResponseEntity.ok(batteryService.getBatteriesByStation(stationId, status));
    }

    @GetMapping("")
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

    @PatchMapping("/{id}/status")
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

@lombok.Data
class UpdateStatusRequest {
    private BatteryStatus status;
    private String reason;
    private boolean adminOverride;
}
