package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DateRangeRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PeakHourAnalysis;
import group8.EVBatterySwapStation_BackEnd.DTO.response.RevenueReportResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapAnalyticsResponse;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    
    // Revenue analytics
    RevenueReportResponse getRevenueReport(DateRangeRequest request);
    Map<String, Object> getRevenueByStation(DateRangeRequest request);
    Map<String, Object> getRevenueByPaymentMethod(DateRangeRequest request);
    
    // Swap analytics
    SwapAnalyticsResponse getSwapAnalytics(DateRangeRequest request);
    List<PeakHourAnalysis> getPeakHoursAnalysis(DateRangeRequest request);
    Map<String, Object> getSwapHeatmapData(DateRangeRequest request);
    Map<String, Object> getSwapFrequencyReport(DateRangeRequest request);
    
    // Dashboard summary
    Map<String, Object> getDashboardSummary();
}
