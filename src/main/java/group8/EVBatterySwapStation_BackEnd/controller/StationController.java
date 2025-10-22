package group8.EVBatterySwapStation_BackEnd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group8.EVBatterySwapStation_BackEnd.DTO.request.StationRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.StationInfoResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Station;
import group8.EVBatterySwapStation_BackEnd.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StationInfoResponse> createStation(
            @ModelAttribute StationRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StationRequest stationRequest = objectMapper.convertValue(request, StationRequest.class);
        return ResponseEntity.ok(stationService.createStation(stationRequest, file));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Station> updateStation(@PathVariable Long id, @RequestBody StationRequest request, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(stationService.updateStation(id, request, image));
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StationDetailResponse> getStationDetail(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationDetail(id));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<Station>> getAllStations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false, defaultValue = "name,asc") String sort) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(direction, sortParts[0]));
        return ResponseEntity.ok(stationService.getAllStations(pageable));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<StationInfoResponse>> getNearbyStations(@RequestParam double lat, @RequestParam double lon, @RequestParam(defaultValue = "5") double radiusKm) {
        return ResponseEntity.ok(stationService.findNearestStations(lat, lon, radiusKm));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Station>> searchStations(@RequestParam String keyword) {
        return ResponseEntity.ok(stationService.searchStations(keyword));
    }

    @DeleteMapping("/{stationId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationId) {
        stationService.deleteStation(stationId);
        return ResponseEntity.noContent().build();
    }

}
