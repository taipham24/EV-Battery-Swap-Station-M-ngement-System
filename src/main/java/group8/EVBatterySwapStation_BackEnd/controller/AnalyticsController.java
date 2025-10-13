package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DateRangeRequest;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PeakHourAnalysis;
import group8.EVBatterySwapStation_BackEnd.DTO.response.RevenueReportResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapAnalyticsResponse;
import group8.EVBatterySwapStation_BackEnd.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Analytics", description = "Admin endpoints for analytics and reporting")
@PreAuthorize("hasAuthority('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue report")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting revenue report for period: {} to {}", request.getStartDate(), request.getEndDate());

        RevenueReportResponse report = analyticsService.getRevenueReport(request);

        return ResponseEntity.ok(ApiResponse.<RevenueReportResponse>builder()
                .code(200)
                .message("Revenue report retrieved successfully")
                .result(report)
                .build());
    }

    @GetMapping("/revenue/by-station")
    @Operation(summary = "Get revenue by station")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueByStation(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting revenue by station for period: {} to {}", request.getStartDate(), request.getEndDate());

        Map<String, Object> revenueByStation = analyticsService.getRevenueByStation(request);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Revenue by station retrieved successfully")
                .result(revenueByStation)
                .build());
    }

    @GetMapping("/revenue/by-payment-method")
    @Operation(summary = "Get revenue by payment method")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueByPaymentMethod(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting revenue by payment method for period: {} to {}", request.getStartDate(), request.getEndDate());

        Map<String, Object> revenueByPaymentMethod = analyticsService.getRevenueByPaymentMethod(request);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Revenue by payment method retrieved successfully")
                .result(revenueByPaymentMethod)
                .build());
    }

    @GetMapping("/swaps")
    @Operation(summary = "Get swap analytics")
    public ResponseEntity<ApiResponse<SwapAnalyticsResponse>> getSwapAnalytics(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting swap analytics for period: {} to {}", request.getStartDate(), request.getEndDate());

        SwapAnalyticsResponse analytics = analyticsService.getSwapAnalytics(request);

        return ResponseEntity.ok(ApiResponse.<SwapAnalyticsResponse>builder()
                .code(200)
                .message("Swap analytics retrieved successfully")
                .result(analytics)
                .build());
    }

    @GetMapping("/swaps/peak-hours")
    @Operation(summary = "Get peak hours analysis")
    public ResponseEntity<ApiResponse<List<PeakHourAnalysis>>> getPeakHoursAnalysis(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting peak hours analysis for period: {} to {}", request.getStartDate(), request.getEndDate());

        List<PeakHourAnalysis> peakHours = analyticsService.getPeakHoursAnalysis(request);

        return ResponseEntity.ok(ApiResponse.<List<PeakHourAnalysis>>builder()
                .code(200)
                .message("Peak hours analysis retrieved successfully")
                .result(peakHours)
                .build());
    }

    @GetMapping("/swaps/heatmap")
    @Operation(summary = "Get swap heatmap data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSwapHeatmapData(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting swap heatmap data for period: {} to {}", request.getStartDate(), request.getEndDate());

        Map<String, Object> heatmapData = analyticsService.getSwapHeatmapData(request);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Swap heatmap data retrieved successfully")
                .result(heatmapData)
                .build());
    }

    @GetMapping("/swaps/frequency")
    @Operation(summary = "Get swap frequency report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSwapFrequencyReport(
            @Parameter(description = "Date range and period") @Valid DateRangeRequest request) {
        log.info("Admin getting swap frequency report for period: {} to {}", request.getStartDate(), request.getEndDate());

        Map<String, Object> frequencyReport = analyticsService.getSwapFrequencyReport(request);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Swap frequency report retrieved successfully")
                .result(frequencyReport)
                .build());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary() {
        log.info("Admin getting dashboard summary");

        Map<String, Object> summary = analyticsService.getDashboardSummary();

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Dashboard summary retrieved successfully")
                .result(summary)
                .build());
    }
}
