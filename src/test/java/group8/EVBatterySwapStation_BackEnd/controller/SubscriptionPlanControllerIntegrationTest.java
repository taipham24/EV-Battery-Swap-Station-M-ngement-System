//package group8.EVBatterySwapStation_BackEnd.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import group8.EVBatterySwapStation_BackEnd.DTO.request.SubscriptionPlanRequest;
//import group8.EVBatterySwapStation_BackEnd.entity.*;
//import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
//import group8.EVBatterySwapStation_BackEnd.repository.*;
//import group8.EVBatterySwapStation_BackEnd.service.SubscriptionPlanService;
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
//class SubscriptionPlanControllerIntegrationTest {
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
//    private SubscriptionPlanService subscriptionPlanService;
//
//    @Autowired
//    private DriverRepository driverRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private SubscriptionPlanRepository subscriptionPlanRepository;
//
//    @Autowired
//    private DriverSubscriptionRepository driverSubscriptionRepository;
//
//    @Autowired
//    private BatteryRepository batteryRepository;
//
//    private Driver testDriver;
//    private RoleDetail adminRole;
//    private SubscriptionPlan testPlan;
//    private DriverSubscription testSubscription;
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
//        // Create test subscription plan
//        testPlan = new SubscriptionPlan();
//        testPlan.setName("Test Plan");
//        testPlan.setDescription("Test Description");
//        testPlan.setPrice(100000.0);
//        testPlan.setDurationDays(30);
//        testPlan.setSwapLimit(10);
//        testPlan.setPricePerSwap(10000.0);
//        testPlan.setPricePerExtraSwap(15000.0);
//        testPlan.setActive(true);
//        testPlan.setDisplayOrder(1);
//        testPlan.setCreatedAt(LocalDateTime.now());
//        testPlan.setUpdatedAt(LocalDateTime.now());
//        testPlan = subscriptionPlanRepository.save(testPlan);
//
//        // Create test battery
//        Battery testBattery = new Battery();
//        testBattery.setSerialNumber("TEST-BAT-001");
//        testBattery.setStatus(BatteryStatus.AVAILABLE);
//        testBattery.setCapacityWh(50000);
//        testBattery.setModel("Test Battery Model");
//        testBattery = batteryRepository.save(testBattery);
//
//        // Create test subscription
//        testSubscription = new DriverSubscription();
//        testSubscription.setDriver(testDriver);
//        testSubscription.setPlan(testPlan);
//        testSubscription.setBattery(testBattery);
//        testSubscription.setActive(true);
//        testSubscription.setSwapsUsed(5);
//        testSubscription.setStartDate(LocalDateTime.now().minusDays(15));
//        testSubscription.setEndDate(LocalDateTime.now().plusDays(15));
//        testSubscription.setAutoRenew(true);
//        testSubscription = driverSubscriptionRepository.save(testSubscription);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void createSubscriptionPlan_AsAdmin_ShouldCreatePlan() throws Exception {
//        // Given
//        SubscriptionPlanRequest request = new SubscriptionPlanRequest();
//        request.setName("New Plan");
//        request.setDescription("New Description");
//        request.setPrice(150000.0);
//        request.setDurationDays(60);
//        request.setSwapLimit(20);
//        request.setPricePerSwap(12000.0);
//        request.setPricePerExtraSwap(18000.0);
//
//        SubscriptionPlan mockPlan = new SubscriptionPlan();
//        mockPlan.setPlanId(2L);
//        mockPlan.setName("New Plan");
//        mockPlan.setPrice(150000.0);
//        mockPlan.setActive(true);
//
//        when(subscriptionPlanService.createSubscription(any(SubscriptionPlan.class)))
//                .thenReturn(mockPlan);
//
//        // When & Then
//        mockMvc.perform(post("/api/subscription-plans/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Subscription plan created successfully"))
//                .andExpect(jsonPath("$.result.name").value("New Plan"));
//    }
//
//    @Test
//    @WithMockUser(roles = "DRIVER")
//    void createSubscriptionPlan_AsDriver_ShouldReturnForbidden() throws Exception {
//        // Given
//        SubscriptionPlanRequest request = new SubscriptionPlanRequest();
//        request.setName("New Plan");
//        request.setPrice(150000.0);
//
//        // When & Then
//        mockMvc.perform(post("/api/subscription-plans/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getAllPlans_AsAdmin_ShouldReturnAllPlans() throws Exception {
//        // Given
//        List<SubscriptionPlan> mockPlans = Arrays.asList(testPlan);
//        when(subscriptionPlanService.getAllPlans())
//                .thenReturn(mockPlans);
//
//        // When & Then
//        mockMvc.perform(get("/api/subscription-plans/all")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result").isArray())
//                .andExpect(jsonPath("$.result[0].name").value("Test Plan"));
//    }
//
//    @Test
//    void getPlansSortedByPrice_WithoutAuth_ShouldReturnActivePlans() throws Exception {
//        // Given
//        List<SubscriptionPlan> mockPlans = Arrays.asList(testPlan);
//        when(subscriptionPlanService.getAllPlansSortedByPrice())
//                .thenReturn(mockPlans);
//
//        // When & Then
//        mockMvc.perform(get("/api/subscription-plans/sorted")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result").isArray())
//                .andExpect(jsonPath("$.result[0].name").value("Test Plan"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateSubscriptionPlan_AsAdmin_ShouldUpdatePlan() throws Exception {
//        // Given
//        SubscriptionPlanRequest request = new SubscriptionPlanRequest();
//        request.setName("Updated Plan");
//        request.setDescription("Updated Description");
//        request.setPrice(200000.0);
//        request.setDurationDays(90);
//        request.setSwapLimit(30);
//        request.setPricePerSwap(15000.0);
//        request.setPricePerExtraSwap(20000.0);
//
//        SubscriptionPlan mockUpdatedPlan = new SubscriptionPlan();
//        mockUpdatedPlan.setPlanId(1L);
//        mockUpdatedPlan.setName("Updated Plan");
//        mockUpdatedPlan.setPrice(200000.0);
//
//        when(subscriptionPlanService.update(eq(1L), any(SubscriptionPlan.class)))
//                .thenReturn(mockUpdatedPlan);
//
//        // When & Then
//        mockMvc.perform(put("/api/subscription-plans/update/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.name").value("Updated Plan"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deactivatePlan_AsAdmin_ShouldDeactivatePlan() throws Exception {
//        // When & Then
//        mockMvc.perform(delete("/api/subscription-plans/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Subscription plan deactivated successfully"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getPlanSubscribers_AsAdmin_ShouldReturnSubscribers() throws Exception {
//        // Given
//        Page<DriverSubscription> mockPage = new PageImpl<>(Arrays.asList(testSubscription));
//        when(subscriptionPlanService.getPlanSubscribers(eq(1L), any()))
//                .thenReturn(mockPage);
//
//        // When & Then
//        mockMvc.perform(get("/api/subscription-plans/1/subscribers")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.content").isArray())
//                .andExpect(jsonPath("$.result.content[0].active").value(true));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getPlanStatistics_AsAdmin_ShouldReturnStatistics() throws Exception {
//        // Given
//        Map<String, Object> mockStats = new HashMap<>();
//        mockStats.put("planId", 1L);
//        mockStats.put("planName", "Test Plan");
//        mockStats.put("totalSubscriptions", 1L);
//        mockStats.put("activeSubscriptions", 1L);
//        mockStats.put("totalSwapsUsed", 5L);
//        mockStats.put("averageSwapsPerSubscription", 5.0);
//
//        when(subscriptionPlanService.getPlanStatistics(1L))
//                .thenReturn(mockStats);
//
//        // When & Then
//        mockMvc.perform(get("/api/subscription-plans/1/statistics")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.planName").value("Test Plan"))
//                .andExpect(jsonPath("$.result.totalSubscriptions").value(1))
//                .andExpect(jsonPath("$.result.activeSubscriptions").value(1));
//    }
//
//    @Test
//    @WithMockUser(roles = "STAFF")
//    void getAllPlans_AsStaff_ShouldReturnForbidden() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/subscription-plans/all")
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void createSubscriptionPlan_WithInvalidData_ShouldReturnBadRequest() throws Exception {
//        // Given
//        SubscriptionPlanRequest invalidRequest = new SubscriptionPlanRequest();
//        invalidRequest.setName(""); // Empty name
//        invalidRequest.setPrice(-100.0); // Negative price
//
//        // When & Then
//        mockMvc.perform(post("/api/subscription-plans/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest))
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateSubscriptionPlan_WithInvalidData_ShouldReturnBadRequest() throws Exception {
//        // Given
//        SubscriptionPlanRequest invalidRequest = new SubscriptionPlanRequest();
//        invalidRequest.setName(""); // Empty name
//        invalidRequest.setPrice(-100.0); // Negative price
//
//        // When & Then
//        mockMvc.perform(put("/api/subscription-plans/update/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest))
//                        .with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deactivateNonExistentPlan_ShouldHandleGracefully() throws Exception {
//        // When & Then
//        mockMvc.perform(delete("/api/subscription-plans/999")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getPlanSubscribers_WithPagination_ShouldReturnPaginatedResults() throws Exception {
//        // Given
//        Page<DriverSubscription> mockPage = new PageImpl<>(Arrays.asList(testSubscription));
//        when(subscriptionPlanService.getPlanSubscribers(eq(1L), any()))
//                .thenReturn(mockPage);
//
//        // When & Then
//        mockMvc.perform(get("/api/subscription-plans/1/subscribers")
//                        .param("page", "0")
//                        .param("size", "5")
//                        .param("sort", "startDate,desc")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.result.content").isArray())
//                .andExpect(jsonPath("$.result.pageable.pageNumber").value(0))
//                .andExpect(jsonPath("$.result.pageable.pageSize").value(5));
//    }
//}
