package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.BatteryTransferRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryTransferDTO;
import group8.EVBatterySwapStation_BackEnd.enums.TransferStatus;
import group8.EVBatterySwapStation_BackEnd.service.BatteryTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battery-transfers")
@RequiredArgsConstructor
public class BatteryTransferController {
    
    private final BatteryTransferService batteryTransferService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatteryTransferDTO> initiateTransfer(@Valid @RequestBody BatteryTransferRequest request) {
        return ResponseEntity.ok(batteryTransferService.initiateTransfer(request));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<BatteryTransferDTO> completeTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(batteryTransferService.completeTransfer(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatteryTransferDTO> cancelTransfer(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(batteryTransferService.cancelTransfer(id, reason));
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BatteryTransferDTO>> listTransfers(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) TransferStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(batteryTransferService.listTransfers(stationId, status, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatteryTransferDTO> getTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(batteryTransferService.getTransferById(id));
    }
}
