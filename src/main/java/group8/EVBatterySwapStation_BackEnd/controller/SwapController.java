package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.InspectReturnRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.SwapPaymentRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.BatteryInspectionDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PaymentDTO;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapTransactionDTO;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.service.SwapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/swaps")
@RequiredArgsConstructor
public class SwapController {
    private final SwapService swapService;

    @PreAuthorize("hasAuthority('STAFF') or hasAuthority('MANAGER')")
    @PostMapping("/{bookingId}/confirm")
    public ApiResponse<SwapTransactionDTO> confirm(@PathVariable Long bookingId) {
        return ApiResponse.success(swapService.confirm(bookingId));
    }

    @PreAuthorize("hasAuthority('STAFF') or hasAuthority('MANAGER')")
    @PostMapping("/{swapId}/pay")
    public ApiResponse<PaymentDTO> pay(@PathVariable Long swapId, @RequestBody SwapPaymentRequest request) {
        return ApiResponse.success(swapService.pay(swapId, request));
    }

    @PreAuthorize("hasAuthority('STAFF') or hasAuthority('MANAGER')")
    @PostMapping("/{swapId}/inspect-return")
    public ApiResponse<BatteryInspectionDTO> inspect(@PathVariable Long swapId, @RequestBody InspectReturnRequest request) {
        return ApiResponse.success(swapService.inspectReturn(swapId, request));
    }

    @PreAuthorize("hasAuthority('STAFF') or hasAuthority('MANAGER')")
    @GetMapping("")
    public ApiResponse<Page<SwapTransactionDTO>> list(
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] sp = sort.split(",");
        Sort s = sp.length == 2 && sp[1].equalsIgnoreCase("asc")
                ? Sort.by(sp[0]).ascending() : Sort.by(sp[0]).descending();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 100), s);
        return ApiResponse.success(swapService.list(driverId, stationId, status, pageable));
    }
}




