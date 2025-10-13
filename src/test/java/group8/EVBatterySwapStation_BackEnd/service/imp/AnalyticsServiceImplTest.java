package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.DateRangeRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.PeakHourAnalysis;
import group8.EVBatterySwapStation_BackEnd.DTO.response.RevenueReportResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapAnalyticsResponse;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import group8.EVBatterySwapStation_BackEnd.repository.PaymentRepository;
import group8.EVBatterySwapStation_BackEnd.repository.StationRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SwapTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private SwapTransactionRepository swapTransactionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private DateRangeRequest dateRangeRequest;
    private SwapTransaction testSwap;
    private Station testStation;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // Setup date range request
        dateRangeRequest = DateRangeRequest.builder()
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now())
                .period("MONTHLY")
                .stationId(null)
                .groupBy("DAY")
                .build();

        // Setup test station
        testStation = new Station();
        testStation.setStationId(1L);
        testStation.setName("Test Station");

        // Setup test swap transaction
        testSwap = new SwapTransaction();
        testSwap.setSwapId(1L);
        testSwap.setStation(testStation);
        testSwap.setAmountVnd(100000L);
        testSwap.setStatus(SwapStatus.COMPLETED);
        testSwap.setCreatedAt(LocalDateTime.now());
        testSwap.setPaidAt(LocalDateTime.now());

        // Setup test payment
        testPayment = new Payment();
        testPayment.setPaymentId(1L);
        testPayment.setMethod(PaymentMethod.CREDIT_CARD);
        testPayment.setAmountVnd(100000L);
        testPayment.setPaidAt(LocalDateTime.now());
    }

    @Test
    void getRevenueReport_ValidDateRange_ShouldReturnRevenueReport() {
        // Given
        when(swapTransactionRepository.calculateTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(1000000L);
        when(swapTransactionRepository.calculateDailyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{LocalDateTime.of(2023, 1, 1, 0, 0), 100000L}));
        when(swapTransactionRepository.calculateRevenueByStation(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{1L, "Test Station", 100000L, 10L}));
        when(paymentRepository.calculateRevenueByPaymentMethod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{PaymentMethod.CREDIT_CARD.name(), 100000L, 5L}));

        // When
        RevenueReportResponse result = analyticsService.getRevenueReport(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertEquals(1000000L, result.getTotalRevenue());
        assertNotNull(result.getRevenueByPeriod());
        assertNotNull(result.getRevenueByStation());
        assertNotNull(result.getRevenueByPaymentMethod());
        assertEquals("MONTHLY", result.getPeriodType());
    }

    @Test
    void getRevenueReport_WithStationFilter_ShouldReturnFilteredRevenue() {
        // Given
        dateRangeRequest.setStationId(1L);
        
        when(swapTransactionRepository.calculateTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(500000L);
        when(swapTransactionRepository.calculateDailyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{LocalDateTime.of(2023, 1, 1, 0, 0), 50000L}));
        when(swapTransactionRepository.calculateRevenueByStation(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{1L, "Test Station", 500000L, 5L}));
        when(paymentRepository.calculateRevenueByPaymentMethod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{PaymentMethod.CREDIT_CARD.name(), 500000L, 3L}));

        // When
        RevenueReportResponse result = analyticsService.getRevenueReport(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertEquals(500000L, result.getTotalRevenue());
        assertEquals(1L, result.getRevenueByStation().get(0).getStationId());
    }

    @Test
    void getSwapAnalytics_ValidDateRange_ShouldReturnSwapAnalytics() {
        // Given
        when(swapTransactionRepository.calculateDailySwapCounts(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{LocalDateTime.of(2023, 1, 1, 0, 0), 10L}));
        when(swapTransactionRepository.calculateRevenueByStation(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{1L, "Test Station", 100000L, 10L}));
        when(swapTransactionRepository.calculateHourlyPattern(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{9, 5L, 10000.0}));
        when(swapTransactionRepository.calculateSwapHeatmap(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{9, 1, 5L}));
        when(swapTransactionRepository.calculateSwapsByStatus(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{SwapStatus.COMPLETED.name(), 8L}));

        // When
        SwapAnalyticsResponse result = analyticsService.getSwapAnalytics(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSwapsByPeriod());
        assertNotNull(result.getSwapsByStation());
        assertNotNull(result.getPeakHours());
        assertNotNull(result.getHeatmapData());
        assertNotNull(result.getSwapsByStatus());
        assertEquals("MONTHLY", result.getPeriodType());
    }

    @Test
    void getPeakHoursAnalysis_ValidDateRange_ShouldReturnPeakHours() {
        // Given
        when(swapTransactionRepository.calculateHourlyPattern(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        new Object[]{9, 10L, 10000.0},
                        new Object[]{10, 15L, 12000.0},
                        new Object[]{11, 8L, 9000.0}
                ));

        // When
        List<PeakHourAnalysis> result = analyticsService.getPeakHoursAnalysis(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(10, result.get(0).getHour()); // Highest count first
        assertEquals(15L, result.get(0).getSwapCount());
        assertEquals(12000L, result.get(0).getAverageRevenue());
    }

    @Test
    void getSwapHeatmapData_ValidDateRange_ShouldReturnHeatmapData() {
        // Given
        when(swapTransactionRepository.calculateSwapHeatmap(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        new Object[]{9, 1, 5L},   // Monday 9 AM
                        new Object[]{10, 1, 8L},  // Monday 10 AM
                        new Object[]{9, 2, 3L}   // Tuesday 9 AM
                ));

        // When
        Map<String, Object> result = analyticsService.getSwapHeatmapData(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("heatmapData"));
        assertTrue(result.containsKey("maxValue"));
        assertTrue(result.containsKey("totalSwaps"));
        
        int[][] heatmapData = (int[][]) result.get("heatmapData");
        assertNotNull(heatmapData);
        assertEquals(24, heatmapData.length); // 24 hours
        assertEquals(7, heatmapData[0].length); // 7 days
    }

    @Test
    void getSwapFrequencyReport_ValidDateRange_ShouldReturnFrequencyReport() {
        // Given
        when(swapTransactionRepository.calculateDailySwapCounts(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        new Object[]{LocalDateTime.of(2023, 1, 1, 0, 0), 10L},
                        new Object[]{"2023-01-02", 15L},
                        new Object[]{"2023-01-03", 8L}
                ));

        // When
        Map<String, Object> result = analyticsService.getSwapFrequencyReport(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalSwaps"));
        assertTrue(result.containsKey("averageSwapsPerDay"));
        assertTrue(result.containsKey("peakDay"));
        assertTrue(result.containsKey("lowestDay"));
        assertEquals(33L, result.get("totalSwaps"));
        assertEquals(11L, result.get("averageSwapsPerDay"));
    }

    @Test
    void getRevenueByStation_ValidDateRange_ShouldReturnStationRevenue() {
        // Given
        when(swapTransactionRepository.calculateRevenueByStation(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        new Object[]{1L, "Station 1", 500000L, 50L},
                        new Object[]{2L, "Station 2", 300000L, 30L}
                ));

        // When
        Map<String, Object> result = analyticsService.getRevenueByStation(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalRevenue"));
        assertTrue(result.containsKey("stationBreakdown"));
        assertEquals(800000L, result.get("totalRevenue"));
        
        List<Map<String, Object>> stationBreakdown = (List<Map<String, Object>>) result.get("stationBreakdown");
        assertEquals(2, stationBreakdown.size());
        assertEquals("Station 1", stationBreakdown.get(0).get("stationName"));
        assertEquals(500000.0, stationBreakdown.get(0).get("revenue"));
    }

    @Test
    void getRevenueByPaymentMethod_ValidDateRange_ShouldReturnPaymentMethodRevenue() {
        // Given
        when(paymentRepository.calculateRevenueByPaymentMethod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        new Object[]{PaymentMethod.CREDIT_CARD.name(), 600000L, 30L},
                        new Object[]{PaymentMethod.CASH.name(), 400000L, 20L}
                ));

        // When
        Map<String, Object> result = analyticsService.getRevenueByPaymentMethod(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("paymentMethods"));
        
        Map<String, Map<String, Object>> paymentMethods = (Map<String, Map<String, Object>>) result.get("paymentMethods");
        assertNotNull(paymentMethods);
        assertTrue(paymentMethods.containsKey("CREDIT_CARD"));
        assertTrue(paymentMethods.containsKey("CASH"));
        
        Map<String, Object> creditCardData = paymentMethods.get("CREDIT_CARD");
        assertEquals(600000L, creditCardData.get("revenue"));
        assertEquals(30L, creditCardData.get("count"));
        
        Map<String, Object> cashData = paymentMethods.get("CASH");
        assertEquals(400000L, cashData.get("revenue"));
        assertEquals(20L, cashData.get("count"));
    }

    @Test
    void getDashboardSummary_ShouldReturnDashboardSummary() {
        // Given
        when(swapTransactionRepository.calculateTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(1000000L);
        when(swapTransactionRepository.calculateDailySwapCounts(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{LocalDateTime.of(2023, 1, 1, 0, 0), 10L}));
        when(stationRepository.count())
                .thenReturn(1L);

        // When
        Map<String, Object> result = analyticsService.getDashboardSummary();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalRevenue"));
        assertTrue(result.containsKey("totalSwaps"));
        assertTrue(result.containsKey("totalStations"));
        assertTrue(result.containsKey("averageRevenuePerSwap"));
        assertTrue(result.containsKey("lastUpdated"));
    }

    @Test
    void getRevenueReport_WithCustomPeriod_ShouldHandleCustomPeriod() {
        // Given
        dateRangeRequest.setPeriod("CUSTOM");
        
        when(swapTransactionRepository.calculateTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(500000L);
        when(swapTransactionRepository.calculateDailyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{LocalDateTime.of(2023, 1, 1, 0, 0), 50000L}));
        when(swapTransactionRepository.calculateRevenueByStation(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{1L, "Test Station", 500000L, 5L}));
        when(paymentRepository.calculateRevenueByPaymentMethod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.<Object[]>asList(new Object[]{PaymentMethod.CREDIT_CARD.name(), 500000L, 3L}));

        // When
        RevenueReportResponse result = analyticsService.getRevenueReport(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertEquals("CUSTOM", result.getPeriodType());
        assertEquals(500000L, result.getTotalRevenue());
    }

    @Test
    void getSwapAnalytics_WithEmptyData_ShouldHandleEmptyResults() {
        // Given
        when(swapTransactionRepository.calculateDailySwapCounts(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(swapTransactionRepository.calculateRevenueByStation(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(swapTransactionRepository.calculateHourlyPattern(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(swapTransactionRepository.calculateSwapHeatmap(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(swapTransactionRepository.calculateSwapsByStatus(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        SwapAnalyticsResponse result = analyticsService.getSwapAnalytics(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotalSwaps());
        assertEquals(0.0, result.getAverageSwapsPerDay(), 0.001);
        assertTrue(result.getSwapsByPeriod().isEmpty());
        assertTrue(result.getSwapsByStation().isEmpty());
        assertTrue(result.getPeakHours().isEmpty());
    }

    @Test
    void getPeakHoursAnalysis_WithEmptyData_ShouldReturnEmptyList() {
        // Given
        when(swapTransactionRepository.calculateHourlyPattern(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<PeakHourAnalysis> result = analyticsService.getPeakHoursAnalysis(dateRangeRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
