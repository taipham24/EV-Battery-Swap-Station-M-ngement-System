package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DateRangeRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PeakHourAnalysis;
import group8.EVBatterySwapStation_BackEnd.DTO.response.RevenueReportResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapAnalyticsResponse;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import group8.EVBatterySwapStation_BackEnd.repository.PaymentRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SwapTransactionRepository;
import group8.EVBatterySwapStation_BackEnd.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SwapTransactionRepository swapTransactionRepository;
    private final PaymentRepository paymentRepository;
    private final StationRepository stationRepository;

    @Override
    public RevenueReportResponse getRevenueReport(DateRangeRequest request) {
        log.info("Getting revenue report for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        
        // Calculate total revenue
        Long totalRevenue = swapTransactionRepository.calculateTotalRevenue(startDate, endDate);
        if (totalRevenue == null) totalRevenue = 0L;
        
        // Calculate revenue by period
        Map<String, Long> revenueByPeriod = calculateRevenueByPeriod(startDate, endDate, request.getPeriod());
        
        // Calculate revenue by station
        List<Object[]> stationRevenueData = swapTransactionRepository.calculateRevenueByStation(startDate, endDate);
        List<RevenueReportResponse.StationRevenueDTO> revenueByStation = stationRevenueData.stream()
            .map(row -> RevenueReportResponse.StationRevenueDTO.builder()
                .stationId((Long) row[0])
                .stationName((String) row[1])
                .revenue(((Number) row[2]).longValue())
                .swapCount(((Number) row[3]).longValue())
                .build())
            .collect(Collectors.toList());
        
        // Calculate revenue by payment method
        List<Object[]> paymentMethodData = paymentRepository.calculateRevenueByPaymentMethod(startDate, endDate);
        Map<PaymentMethod, Long> revenueByPaymentMethod = paymentMethodData.stream()
            .collect(Collectors.toMap(
                row -> PaymentMethod.valueOf((String) row[0]),
                row -> ((Number) row[1]).longValue()
            ));
        
        // Calculate growth (compare with previous period)
        Double revenueGrowth = calculateRevenueGrowth(startDate, endDate, request.getPeriod());
        
        return RevenueReportResponse.builder()
            .totalRevenue(totalRevenue)
            .revenueByPeriod(revenueByPeriod)
            .revenueByStation(revenueByStation)
            .revenueByPaymentMethod(revenueByPaymentMethod)
            .revenueGrowth(revenueGrowth)
            .period(request.getPeriod())
            .periodType(request.getPeriod())
            .build();
    }

    @Override
    public Map<String, Object> getRevenueByStation(DateRangeRequest request) {
        log.info("Getting revenue by station for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        List<Object[]> stationRevenueData = swapTransactionRepository.calculateRevenueByStation(
            request.getStartDate(), request.getEndDate());
        
        // Calculate total revenue
        Long totalRevenue = stationRevenueData.stream()
            .mapToLong(row -> ((Number) row[2]).longValue())
            .sum();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("stationBreakdown", stationRevenueData.stream()
            .map(row -> {
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("stationId", row[0]);
                stationData.put("stationName", row[1]);
                stationData.put("revenue", ((Number) row[2]).doubleValue());
                stationData.put("swapCount", ((Number) row[3]).longValue());
                return stationData;
            })
            .collect(Collectors.toList()));
        
        return result;
    }

    @Override
    public Map<String, Object> getRevenueByPaymentMethod(DateRangeRequest request) {
        log.info("Getting revenue by payment method for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        List<Object[]> paymentMethodData = paymentRepository.calculateRevenueByPaymentMethod(
            request.getStartDate(), request.getEndDate());
        
        Map<String, Object> result = new HashMap<>();
        result.put("paymentMethods", paymentMethodData.stream()
            .collect(Collectors.toMap(
                row -> PaymentMethod.valueOf((String) row[0]).name(),
                row -> {
                    Map<String, Object> methodData = new HashMap<>();
                    methodData.put("revenue", ((Number) row[1]).longValue());
                    methodData.put("count", ((Number) row[2]).longValue());
                    return methodData;
                }
            )));
        
        return result;
    }

    @Override
    public SwapAnalyticsResponse getSwapAnalytics(DateRangeRequest request) {
        log.info("Getting swap analytics for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        
        // Calculate total swaps
        List<Object[]> dailySwapCounts = swapTransactionRepository.calculateDailySwapCounts(startDate, endDate);
        long totalSwaps = dailySwapCounts.stream()
            .mapToLong(row -> ((Number) row[1]).longValue())
            .sum();
        
        // Calculate swaps by period
        Map<String, Long> swapsByPeriod = calculateSwapsByPeriod(startDate, endDate, request.getPeriod());
        
        // Calculate swaps by station
        List<Object[]> stationSwapData = swapTransactionRepository.calculateRevenueByStation(startDate, endDate);
        List<SwapAnalyticsResponse.StationSwapDTO> swapsByStation = stationSwapData.stream()
            .map(row -> SwapAnalyticsResponse.StationSwapDTO.builder()
                .stationId((Long) row[0])
                .stationName((String) row[1])
                .swapCount(((Number) row[3]).longValue())
                .revenue(((Number) row[2]).longValue())
                .build())
            .collect(Collectors.toList());
        
        // Calculate average swaps per day
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        double averageSwapsPerDay = totalSwaps / (double) daysBetween;
        
        // Calculate peak hours
        List<Object[]> hourlyPattern = swapTransactionRepository.calculateHourlyPattern(startDate, endDate);
        List<SwapAnalyticsResponse.HourlySwapDTO> peakHours = hourlyPattern.stream()
            .map(row -> SwapAnalyticsResponse.HourlySwapDTO.builder()
                .hour(((Number) row[0]).intValue())
                .swapCount(((Number) row[1]).longValue())
                .averageRevenue(((Number) row[2]).longValue())
                .build())
            .sorted((a, b) -> Long.compare(b.getSwapCount(), a.getSwapCount()))
            .limit(5)
            .collect(Collectors.toList());
        
        // Calculate heatmap data
        List<Object[]> heatmapData = swapTransactionRepository.calculateSwapHeatmap(startDate, endDate);
        Map<String, Long> heatmap = heatmapData.stream()
            .collect(Collectors.toMap(
                row -> row[0] + "_" + row[1], // hour_dayOfWeek
                row -> ((Number) row[2]).longValue()
            ));
        
        // Calculate swaps by status
        List<Object[]> swapsByStatusData = swapTransactionRepository.calculateSwapsByStatus(startDate, endDate);
        Map<SwapStatus, Long> swapsByStatus = swapsByStatusData.stream()
            .collect(Collectors.toMap(
                row -> SwapStatus.valueOf((String) row[0]),
                row -> ((Number) row[1]).longValue()
            ));
        
        return SwapAnalyticsResponse.builder()
            .totalSwaps(totalSwaps)
            .swapsByPeriod(swapsByPeriod)
            .swapsByStation(swapsByStation)
            .averageSwapsPerDay(averageSwapsPerDay)
            .peakHours(peakHours)
            .heatmapData(heatmap)
            .swapsByStatus(swapsByStatus)
            .period(request.getPeriod())
            .periodType(request.getPeriod())
            .build();
    }

    @Override
    public List<PeakHourAnalysis> getPeakHoursAnalysis(DateRangeRequest request) {
        log.info("Getting peak hours analysis for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        List<Object[]> hourlyPattern = swapTransactionRepository.calculateHourlyPattern(
            request.getStartDate(), request.getEndDate());
        
        return hourlyPattern.stream()
            .map(row -> PeakHourAnalysis.builder()
                .hour(((Number) row[0]).intValue())
                .dayOfWeek(0)
                .swapCount(((Number) row[1]).longValue())
                .averageRevenue(((Number) row[2]).longValue())
                .build())
            .sorted(Comparator.comparingLong(PeakHourAnalysis::getSwapCount).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSwapHeatmapData(DateRangeRequest request) {
        log.info("Getting swap heatmap data for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        List<Object[]> heatmapData = swapTransactionRepository.calculateSwapHeatmap(
            request.getStartDate(), request.getEndDate());
        
        // Create 24x7 matrix
        int[][] matrix = new int[24][7];
        long totalSwaps = 0L;
        for (Object[] row : heatmapData) {
            int hour = ((Number) row[0]).intValue();
            int dayOfWeek = ((Number) row[1]).intValue() - 1; // Convert to 0-based
            int count = ((Number) row[2]).intValue();
            matrix[hour][dayOfWeek] = count;
            totalSwaps += count;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("heatmapData", matrix);
        int maxValue = 0;
        for (int[] row : matrix) {
            for (int v : row) {
                if (v > maxValue) maxValue = v;
            }
        }
        result.put("maxValue", maxValue);
        result.put("totalSwaps", totalSwaps);
        
        return result;
    }

    @Override
    public Map<String, Object> getSwapFrequencyReport(DateRangeRequest request) {
        log.info("Getting swap frequency report for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        List<Object[]> dailySwapCounts = swapTransactionRepository.calculateDailySwapCounts(
            request.getStartDate(), request.getEndDate());
        
        Map<String, Object> result = new HashMap<>();
        long totalSwaps = dailySwapCounts.stream()
            .mapToLong(row -> ((Number) row[1]).longValue())
            .sum();
        result.put("totalSwaps", totalSwaps);
        long days = dailySwapCounts.size();
        long average = days > 0 ? Math.round((double) totalSwaps / days) : 0L;
        result.put("averageSwapsPerDay", average);
        
        // Peak and lowest day as strings
        dailySwapCounts.stream()
            .max(Comparator.comparingLong(row -> ((Number) row[1]).longValue()))
            .ifPresent(row -> result.put("peakDay", String.valueOf(row[0])));
        dailySwapCounts.stream()
            .min(Comparator.comparingLong(row -> ((Number) row[1]).longValue()))
            .ifPresent(row -> result.put("lowestDay", String.valueOf(row[0])));
        
        return result;
    }

    @Override
    public Map<String, Object> getDashboardSummary() {
        log.info("Getting dashboard summary");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last30Days = now.minusDays(30);
        
        Map<String, Object> summary = new HashMap<>();
        Long totalRevenue = swapTransactionRepository.calculateTotalRevenue(last30Days, now);
        List<Object[]> swapCounts = swapTransactionRepository.calculateDailySwapCounts(last30Days, now);
        long totalSwaps = swapCounts.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();
        long stationCount = stationRepository.count();
        
        summary.put("totalRevenue", totalRevenue != null ? totalRevenue : 0L);
        summary.put("totalSwaps", totalSwaps);
        summary.put("totalStations", stationCount);
        long avg = (totalRevenue != null && totalSwaps > 0) ? Math.round((double) totalRevenue / totalSwaps) : 0L;
        summary.put("averageRevenuePerSwap", avg);
        summary.put("lastUpdated", now);
        
        return summary;
    }

    private Map<String, Long> calculateRevenueByPeriod(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Object[]> dailyRevenue = swapTransactionRepository.calculateDailyRevenue(startDate, endDate);
        
        Map<String, Long> result = new HashMap<>();
        
        switch (period.toUpperCase()) {
            case "DAILY":
                dailyRevenue.forEach(row -> result.put(row[0].toString(), ((Number) row[1]).longValue()));
                break;
            case "WEEKLY":
                // Group by week
                dailyRevenue.forEach(row -> {
                    LocalDateTime date = (LocalDateTime) row[0];
                    String weekKey = date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                    result.merge(weekKey, ((Number) row[1]).longValue(), Long::sum);
                });
                break;
            case "MONTHLY":
                // Group by month
                dailyRevenue.forEach(row -> {
                    LocalDateTime date = (LocalDateTime) row[0];
                    String monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    result.merge(monthKey, ((Number) row[1]).longValue(), Long::sum);
                });
                break;
            case "YEARLY":
                // Group by year
                dailyRevenue.forEach(row -> {
                    LocalDateTime date = (LocalDateTime) row[0];
                    String yearKey = date.format(DateTimeFormatter.ofPattern("yyyy"));
                    result.merge(yearKey, ((Number) row[1]).longValue(), Long::sum);
                });
                break;
        }
        
        return result;
    }

    private Map<String, Long> calculateSwapsByPeriod(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Object[]> dailySwaps = swapTransactionRepository.calculateDailySwapCounts(startDate, endDate);
        
        Map<String, Long> result = new HashMap<>();
        
        switch (period.toUpperCase()) {
            case "DAILY":
                dailySwaps.forEach(row -> result.put(row[0].toString(), ((Number) row[1]).longValue()));
                break;
            case "WEEKLY":
                dailySwaps.forEach(row -> {
                    LocalDateTime date = (LocalDateTime) row[0];
                    String weekKey = date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                    result.merge(weekKey, ((Number) row[1]).longValue(), Long::sum);
                });
                break;
            case "MONTHLY":
                dailySwaps.forEach(row -> {
                    LocalDateTime date = (LocalDateTime) row[0];
                    String monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    result.merge(monthKey, ((Number) row[1]).longValue(), Long::sum);
                });
                break;
            case "YEARLY":
                dailySwaps.forEach(row -> {
                    LocalDateTime date = (LocalDateTime) row[0];
                    String yearKey = date.format(DateTimeFormatter.ofPattern("yyyy"));
                    result.merge(yearKey, ((Number) row[1]).longValue(), Long::sum);
                });
                break;
        }
        
        return result;
    }

    private Double calculateRevenueGrowth(LocalDateTime startDate, LocalDateTime endDate, String period) {
        // Calculate previous period dates
        LocalDateTime previousStartDate;
        LocalDateTime previousEndDate;
        
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        
        switch (period.toUpperCase()) {
            case "DAILY":
                previousStartDate = startDate.minusDays(1);
                previousEndDate = endDate.minusDays(1);
                break;
            case "WEEKLY":
                previousStartDate = startDate.minusWeeks(1);
                previousEndDate = endDate.minusWeeks(1);
                break;
            case "MONTHLY":
                previousStartDate = startDate.minusMonths(1);
                previousEndDate = endDate.minusMonths(1);
                break;
            case "YEARLY":
                previousStartDate = startDate.minusYears(1);
                previousEndDate = endDate.minusYears(1);
                break;
            default:
                previousStartDate = startDate.minusDays(daysBetween);
                previousEndDate = endDate.minusDays(daysBetween);
                break;
        }
        
        Long currentRevenue = swapTransactionRepository.calculateTotalRevenue(startDate, endDate);
        Long previousRevenue = swapTransactionRepository.calculateTotalRevenue(previousStartDate, previousEndDate);
        
        if (currentRevenue == null) currentRevenue = 0L;
        if (previousRevenue == null || previousRevenue == 0) return 0.0;
        
        return ((currentRevenue - previousRevenue) / (double) previousRevenue) * 100;
    }
}
