//package group8.EVBatterySwapStation_BackEnd.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import group8.EVBatterySwapStation_BackEnd.DTO.request.CustomerFilterRequest;
//import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffAssignmentRequest;
//import group8.EVBatterySwapStation_BackEnd.DTO.request.StaffFilterRequest;
//import group8.EVBatterySwapStation_BackEnd.DTO.request.UpdateCustomerRequest;
//import group8.EVBatterySwapStation_BackEnd.DTO.response.CustomerDetailResponse;
//import group8.EVBatterySwapStation_BackEnd.DTO.response.StaffDetailResponse;
//import group8.EVBatterySwapStation_BackEnd.DTO.response.RevenueReportResponse;
//import group8.EVBatterySwapStation_BackEnd.DTO.response.SwapAnalyticsResponse;
//import group8.EVBatterySwapStation_BackEnd.DTO.response.PeakHourAnalysis;
//import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
//import group8.EVBatterySwapStation_BackEnd.entity.*;
//import group8.EVBatterySwapStation_BackEnd.enums.Role;
//import group8.EVBatterySwapStation_BackEnd.enums.StationStatus;
//import group8.EVBatterySwapStation_BackEnd.repository.*;
//import group8.EVBatterySwapStation_BackEnd.service.CustomerManagementService;
//import group8.EVBatterySwapStation_BackEnd.service.StaffManagementService;
//import group8.EVBatterySwapStation_BackEnd.service.AnalyticsService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//class AdminControllerIntegrationTest {
//
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private CustomerManagementService customerManagementService;
//
//    @MockBean
//    private StaffManagementService staffManagementService;
//
//    @MockBean
//    private AnalyticsService analyticsService;
//
//    @Autowired
//    private DriverRepository driverRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private StationRepository stationRepository;
//
//    @Autowired
//    private StaffProfileRepository staffProfileRepository;
//
//    private Driver testDriver;
//    private RoleDetail adminRole;
//    private Station testStation;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
//                .build();
//
//        // Create admin role
//        adminRole = new RoleDetail();
//        adminRole.setRoleType("ADMIN");
//        adminRole = roleRepository.save(adminRole);
//
//        // Create test driver
//        testDriver = new Driver();
//        testDriver.setUserName("admintest");
//        testDriver.setEmail("admin@test.com");
//        testDriver.setFullName("Admin Test");
//        testDriver.setPassword("password");
//        testDriver.setStatus(true);
//        testDriver.setRoles(Set.of(adminRole));
//        testDriver = driverRepository.save(testDriver);
//
//        // Create test station
//        testStation = new Station();
//        testStation.setName("Test Station");
//        testStation.setAddress("Test Address");
//        testStation.setStatus(StationStatus.ACTIVE);
//        testStation.setCapacity(50);
//        testStation = stationRepository.save(testStation);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getAllCustomers_AsAdmin_ShouldReturnCustomers() throws Exception {
//        // Given
//        CustomerDetailResponse mockCustomer = CustomerDetailResponse.builder()
//                .driverId(1L)
//                .userName("testuser")
//                .email("test@example.com")
//                .fullName("Test User")
//                .suspended(false)
//                .build();
//
//        Page<CustomerDetailResponse> mockPage = new PageImpl<>(Arrays.asList(mockCustomer));
//        when(customerManagementService.getAllCustomers(any(CustomerFilterRequest.class)))
//                .thenReturn(mockPage);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/customers")
//                        .param("keyword", "test")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Customers retrieved successfully"))
//                .andExpect(jsonPath("$.result.content").isArray())
//                .andExpect(jsonPath("$.result.content[0].userName").value("testuser"));
//    }
//
//    @Test
//    @WithMockUser(roles = "DRIVER")
//    void getAllCustomers_AsDriver_ShouldReturnForbidden() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/admin/customers")
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getCustomerDetail_AsAdmin_ShouldReturnCustomerDetails() throws Exception {
//        // Given
//        CustomerDetailResponse mockCustomer = CustomerDetailResponse.builder()
//                .driverId(1L)
//                .userName("testuser")
//                .email("test@example.com")
//                .fullName("Test User")
//                .suspended(false)
//                .build();
//
//        when(customerManagementService.getCustomerDetail(1L))
//                .thenReturn(mockCustomer);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/customers/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.userName").value("testuser"))
//                .andExpect(jsonPath("$.result.email").value("test@example.com"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateCustomer_AsAdmin_ShouldUpdateCustomer() throws Exception {
//        // Given
//        UpdateCustomerRequest updateRequest = UpdateCustomerRequest.builder()
//                .fullName("Updated Name")
//                .email("updated@example.com")
//                .phone("1234567890")
//                .address("Updated Address")
//                .build();
//
//        CustomerDetailResponse mockResponse = CustomerDetailResponse.builder()
//                .driverId(1L)
//                .userName("testuser")
//                .email("updated@example.com")
//                .fullName("Updated Name")
//                .build();
//
//        when(customerManagementService.updateCustomer(eq(1L), any(UpdateCustomerRequest.class)))
//                .thenReturn(mockResponse);
//
//        // When & Then
//        mockMvc.perform(put("/api/admin/customers/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.fullName").value("Updated Name"))
//                .andExpect(jsonPath("$.result.email").value("updated@example.com"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void suspendCustomer_AsAdmin_ShouldSuspendCustomer() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/api/admin/customers/1/suspend")
//                        .param("reason", "Policy violation")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Customer suspended successfully"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deleteCustomer_AsAdmin_ShouldSoftDeleteCustomer() throws Exception {
//        // When & Then
//        mockMvc.perform(delete("/api/admin/customers/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void assignStaffToStation_AsAdmin_ShouldAssignStaff() throws Exception {
//        // Given
//        StaffAssignmentRequest assignmentRequest = StaffAssignmentRequest.builder()
//                .driverId(1L)
//                .stationId(1L)
//                .workShift("Morning")
//                .notes("Test assignment")
//                .active(true)
//                .build();
//
//        StaffDetailResponse mockResponse = StaffDetailResponse.builder()
//                .staffId(1L)
//                .driverId(1L)
//                .userName("staffuser")
//                .fullName("Staff User")
//                .stationId(1L)
//                .stationName("Test Station")
//                .workShift("Morning")
//                .active(true)
//                .build();
//
//        when(staffManagementService.assignStaffToStation(any(StaffAssignmentRequest.class)))
//                .thenReturn(mockResponse);
//
//        // When & Then
//        mockMvc.perform(post("/api/admin/staff/assign")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(assignmentRequest))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.userName").value("staffuser"))
//                .andExpect(jsonPath("$.result.stationName").value("Test Station"))
//                .andExpect(jsonPath("$.result.workShift").value("Morning"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getAllStaff_AsAdmin_ShouldReturnStaffList() throws Exception {
//        // Given
//        StaffDetailResponse mockStaff = StaffDetailResponse.builder()
//                .staffId(1L)
//                .driverId(1L)
//                .userName("staffuser")
//                .fullName("Staff User")
//                .stationId(1L)
//                .stationName("Test Station")
//                .workShift("Morning")
//                .active(true)
//                .build();
//
//        Page<StaffDetailResponse> mockPage = new PageImpl<>(Arrays.asList(mockStaff));
//        when(staffManagementService.getAllStaff(any(StaffFilterRequest.class)))
//                .thenReturn(mockPage);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/staff")
//                        .param("stationId", "1")
//                        .param("active", "true")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.content").isArray())
//                .andExpect(jsonPath("$.result.content[0].userName").value("staffuser"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getStaffDetail_AsAdmin_ShouldReturnStaffDetails() throws Exception {
//        // Given
//        StaffDetailResponse mockStaff = StaffDetailResponse.builder()
//                .staffId(1L)
//                .driverId(1L)
//                .userName("staffuser")
//                .fullName("Staff User")
//                .stationId(1L)
//                .stationName("Test Station")
//                .workShift("Morning")
//                .active(true)
//                .build();
//
//        when(staffManagementService.getStaffDetail(1L))
//                .thenReturn(mockStaff);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/staff/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.userName").value("staffuser"))
//                .andExpect(jsonPath("$.result.stationName").value("Test Station"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void removeStaffFromStation_AsAdmin_ShouldRemoveStaff() throws Exception {
//        // When & Then
//        mockMvc.perform(delete("/api/admin/staff/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Staff removed from station successfully"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getRevenueReport_AsAdmin_ShouldReturnRevenueReport() throws Exception {
//        // Given
//        RevenueReportResponse mockReport = RevenueReportResponse.builder()
//                .totalRevenue(1000000L)
//                .revenueGrowth(15.5)
//                .periodType("MONTHLY")
//                .build();
//
//        when(analyticsService.getRevenueReport(any()))
//                .thenReturn(mockReport);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/analytics/revenue")
//                        .param("startDate", "2023-01-01T00:00:00")
//                        .param("endDate", "2023-01-31T23:59:59")
//                        .param("period", "MONTHLY")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.totalRevenue").value(1000000.0))
//                .andExpect(jsonPath("$.result.periodType").value("MONTHLY"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getSwapAnalytics_AsAdmin_ShouldReturnSwapAnalytics() throws Exception {
//        // Given
//        SwapAnalyticsResponse mockAnalytics = SwapAnalyticsResponse.builder()
//                .totalSwaps(100L)
//                .averageSwapsPerDay(3.2)
//                .periodType("MONTHLY")
//                .build();
//
//        when(analyticsService.getSwapAnalytics(any()))
//                .thenReturn(mockAnalytics);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/analytics/swaps")
//                        .param("startDate", "2023-01-01T00:00:00")
//                        .param("endDate", "2023-01-31T23:59:59")
//                        .param("period", "MONTHLY")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.totalSwaps").value(100))
//                .andExpect(jsonPath("$.result.periodType").value("MONTHLY"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getDashboardSummary_AsAdmin_ShouldReturnDashboardSummary() throws Exception {
//        // Given
//        Map<String, Object> mockSummary = new HashMap<>();
//        mockSummary.put("totalCustomers", 150L);
//        mockSummary.put("totalStaff", 25L);
//        mockSummary.put("totalRevenue", 5000000.0);
//        mockSummary.put("totalSwaps", 500L);
//
//        when(analyticsService.getDashboardSummary())
//                .thenReturn(mockSummary);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/analytics/dashboard")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.totalCustomers").value(150))
//                .andExpect(jsonPath("$.result.totalStaff").value(25))
//                .andExpect(jsonPath("$.result.totalRevenue").value(5000000.0));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getPeakHoursAnalysis_AsAdmin_ShouldReturnPeakHours() throws Exception {
//        // Given
//        List<PeakHourAnalysis> mockPeakHours = Arrays.asList(
//                PeakHourAnalysis.builder()
//                        .hour(9)
//                        .dayOfWeek(1)
//                        .swapCount(25L)
//                        .averageRevenue(15000L)
//                        .build(),
//                PeakHourAnalysis.builder()
//                        .hour(10)
//                        .dayOfWeek(1)
//                        .swapCount(30L)
//                        .averageRevenue(18000L)
//                        .build()
//        );
//
//        when(analyticsService.getPeakHoursAnalysis(any()))
//                .thenReturn(mockPeakHours);
//
//        // When & Then
//        mockMvc.perform(get("/api/admin/analytics/swaps/peak-hours")
//                        .param("startDate", "2023-01-01T00:00:00")
//                        .param("endDate", "2023-01-31T23:59:59")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result").isArray())
//                .andExpect(jsonPath("$.result[0].hour").value(9))
//                .andExpect(jsonPath("$.result[0].swapCount").value(25));
//    }
//
//    @Test
//    @WithMockUser(roles = "STAFF")
//    void getAllCustomers_AsStaff_ShouldReturnForbidden() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/admin/customers")
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void getAllCustomers_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/admin/customers")
//                        .with(csrf()))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateCustomer_WithInvalidData_ShouldReturnBadRequest() throws Exception {
//        // Given
//        UpdateCustomerRequest invalidRequest = UpdateCustomerRequest.builder()
//                .email("invalid-email") // Invalid email format
//                .build();
//
//        // When & Then
//        mockMvc.perform(put("/api/admin/customers/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest))
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void assignStaffToStation_WithInvalidData_ShouldReturnBadRequest() throws Exception {
//        // Given
//        StaffAssignmentRequest invalidRequest = StaffAssignmentRequest.builder()
//                .driverId(null) // Missing required field
//                .stationId(1L)
//                .workShift("")
//                .build();
//
//        // When & Then
//        mockMvc.perform(post("/api/admin/staff/assign")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest))
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//}
