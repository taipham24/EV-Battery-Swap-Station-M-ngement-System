package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.CustomerFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.UpdateCustomerRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.CustomerDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SubscriptionHistoryResponse;
import group8.EVBatterySwapStation_BackEnd.entity.*;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import group8.EVBatterySwapStation_BackEnd.enums.Role;
import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverSubscriptionRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SwapTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerManagementServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverSubscriptionRepository driverSubscriptionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SwapTransactionRepository swapTransactionRepository;

    @InjectMocks
    private CustomerManagementServiceImpl customerManagementService;

    private Driver testDriver;
    private RoleDetail driverRole;
    private DriverSubscription testSubscription;
    private SwapTransaction testSwap;
    private CustomerFilterRequest filterRequest;
    private UpdateCustomerRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test driver
        testDriver = new Driver();
        testDriver.setDriverId(1L);
        testDriver.setUserName("testuser");
        testDriver.setEmail("test@example.com");
        testDriver.setFullName("Test User");
        testDriver.setStatus(true);
        testDriver.setDeleted(false);
        testDriver.setSuspended(false);
        testDriver.setCreatedAt(Instant.now());
        testDriver.setUpdatedAt(Instant.now());
        testDriver.setLastLogin(LocalDateTime.now());

        // Setup driver role
        driverRole = new RoleDetail();
        driverRole.setUserRoleId(1L);
        driverRole.setRoleType("DRIVER");

        testDriver.setRoles(Set.of(driverRole));

        // Setup test subscription
        testSubscription = new DriverSubscription();
        testSubscription.setSubscriptionId(1L);
        testSubscription.setDriver(testDriver);
        testSubscription.setActive(true);
        testSubscription.setSwapsUsed(5);
        testSubscription.setStartDate(LocalDateTime.now().minusDays(30));
        testSubscription.setEndDate(LocalDateTime.now().plusDays(30));

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setPlanId(1L);
        plan.setName("Basic Plan");
        plan.setSwapLimit(10);
        testSubscription.setPlan(plan);

        // Setup test battery
        Battery testBattery = new Battery();
        testBattery.setBatteryId(1L);
        testBattery.setSerialNumber("BAT001");
        testBattery.setCapacityWh(50000);
        testBattery.setModel("Test Battery Model");
        testBattery.setStatus(BatteryStatus.AVAILABLE);
        testSubscription.setBattery(testBattery);

        // Setup test swap transaction
        testSwap = new SwapTransaction();
        testSwap.setSwapId(1L);
        testSwap.setDriver(testDriver);
        testSwap.setAmountVnd(100000L);
        testSwap.setStatus(SwapStatus.COMPLETED);
        testSwap.setPaidAt(LocalDateTime.now());

        // Setup filter request
        filterRequest = CustomerFilterRequest.builder()
                .keyword("test")
                .deleted(false)
                .suspended(false)
                .hasActiveSubscription(true)
                .page(0)
                .size(10)
                .sortBy("createdAt")
                .sortDirection("desc")
                .build();

        // Setup update request
        updateRequest = UpdateCustomerRequest.builder()
                .fullName("Updated Name")
                .email("updated@example.com")
                .phone("1234567890")
                .address("Updated Address")
                .suspended(false)
                .build();

        // Setup mocks
        when(roleRepository.findByRoleType("DRIVER")).thenReturn(Optional.of(driverRole));
    }

    @Test
    void getAllCustomers_WithFilters_ShouldReturnPaginatedResults() {
        // Given
        List<Driver> drivers = Arrays.asList(testDriver);
        Page<Driver> driverPage = new PageImpl<>(drivers);
        
        when(driverRepository.searchCustomers(anyString(), any(Pageable.class)))
                .thenReturn(driverPage);
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(anyLong()))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        Page<CustomerDetailResponse> result = customerManagementService.getAllCustomers(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUserName());
        verify(driverRepository).searchCustomers(eq("test"), any(Pageable.class));
    }

    @Test
    void getAllCustomers_WithNoKeyword_ShouldReturnAllCustomers() {
        // Given
        filterRequest.setKeyword(null);
        List<Driver> drivers = Arrays.asList(testDriver);
        Page<Driver> driverPage = new PageImpl<>(drivers);
        
        when(driverRepository.findByDeletedFalseAndRolesContaining(any(RoleDetail.class), any(Pageable.class)))
                .thenReturn(driverPage);
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(anyLong()))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        Page<CustomerDetailResponse> result = customerManagementService.getAllCustomers(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(driverRepository).findByDeletedFalseAndRolesContaining(any(RoleDetail.class), any(Pageable.class));
    }

    @Test
    void getCustomerDetail_ValidId_ShouldReturnCustomerDetails() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverSubscriptionRepository.findByDriverAndActiveTrue(testDriver))
                .thenReturn(Optional.of(testSubscription));
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(1L))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(1L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        CustomerDetailResponse result = customerManagementService.getCustomerDetail(1L);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getFullName());
        assertTrue(result.getSubscriptionSummary().isHasActiveSubscription());
        assertEquals(1L, result.getTotalSwaps());
        assertEquals(100000L, result.getTotalRevenue());
    }

    @Test
    void getCustomerDetail_InvalidId_ShouldThrowException() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(999L))
                .thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> customerManagementService.getCustomerDetail(999L));
        assertEquals(ErrorCode.DRIVER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateCustomer_ValidRequest_ShouldUpdateCustomer() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(testDriver);
        when(driverSubscriptionRepository.findByDriverAndActiveTrue(testDriver))
                .thenReturn(Optional.of(testSubscription));
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(1L))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(1L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        CustomerDetailResponse result = customerManagementService.updateCustomer(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        assertEquals("updated@example.com", result.getEmail());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void updateCustomer_InvalidId_ShouldThrowException() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(999L))
                .thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, 
                () -> customerManagementService.updateCustomer(999L, updateRequest));
        assertEquals(ErrorCode.DRIVER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void suspendCustomer_ValidId_ShouldSuspendCustomer() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(testDriver);

        // When
        customerManagementService.suspendCustomer(1L, "Policy violation");

        // Then
        verify(driverRepository).save(argThat(driver -> 
                driver.isSuspended() && 
                "Policy violation".equals(driver.getSuspensionReason())));
    }

    @Test
    void unsuspendCustomer_ValidId_ShouldUnsuspendCustomer() {
        // Given
        testDriver.setSuspended(true);
        testDriver.setSuspensionReason("Previous violation");
        
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(testDriver);

        // When
        customerManagementService.unsuspendCustomer(1L);

        // Then
        verify(driverRepository).save(argThat(driver -> 
                !driver.isSuspended() && 
                driver.getSuspensionReason() == null));
    }

    @Test
    void deleteCustomer_ValidId_ShouldSoftDeleteCustomer() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(testDriver);

        // When
        customerManagementService.deleteCustomer(1L);

        // Then
        verify(driverRepository).save(argThat(driver -> driver.isDeleted()));
    }

    @Test
    void getCustomerSubscriptionHistory_ValidId_ShouldReturnHistory() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(1L))
                .thenReturn(Arrays.asList(testSubscription));

        // When
        List<SubscriptionHistoryResponse> result = customerManagementService.getCustomerSubscriptionHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Basic Plan", result.get(0).getPlanName());
        assertEquals(5, result.get(0).getSwapsUsed());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void getCustomerStatistics_ValidId_ShouldReturnStatistics() {
        // Given
        when(driverRepository.findByDriverIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(testDriver));
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(1L))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(1L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        Map<String, Object> result = customerManagementService.getCustomerStatistics(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalSwaps"));
        assertTrue(result.containsKey("totalRevenue"));
        assertTrue(result.containsKey("totalSubscriptions"));
        assertEquals(1L, result.get("totalSwaps"));
        assertEquals(100000L, result.get("totalRevenue"));
    }

    @Test
    void getAllCustomers_WithSuspendedFilter_ShouldReturnOnlySuspendedCustomers() {
        // Given
        testDriver.setSuspended(true);
        testDriver.setSuspensionReason("Policy violation");
        
        List<Driver> drivers = Arrays.asList(testDriver);
        Page<Driver> driverPage = new PageImpl<>(drivers);
        
        filterRequest.setSuspended(true);
        filterRequest.setKeyword(null);
        
        when(driverRepository.findByDeletedFalseAndRolesContaining(any(RoleDetail.class), any(Pageable.class)))
                .thenReturn(driverPage);
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(anyLong()))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        Page<CustomerDetailResponse> result = customerManagementService.getAllCustomers(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).isSuspended());
    }

    @Test
    void getAllCustomers_WithHasActiveSubscriptionFilter_ShouldReturnOnlySubscribedCustomers() {
        // Given
        List<Driver> drivers = Arrays.asList(testDriver);
        Page<Driver> driverPage = new PageImpl<>(drivers);
        
        filterRequest.setHasActiveSubscription(true);
        filterRequest.setKeyword(null);
        
        when(driverRepository.findByDeletedFalseAndRolesContaining(any(RoleDetail.class), any(Pageable.class)))
                .thenReturn(driverPage);
        when(driverSubscriptionRepository.findByDriverAndActiveTrue(testDriver))
                .thenReturn(Optional.of(testSubscription));
        when(driverSubscriptionRepository.findByDriverIdOrderByStartDateDesc(anyLong()))
                .thenReturn(Arrays.asList(testSubscription));
        when(swapTransactionRepository.findByDriver_DriverId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testSwap)));

        // When
        Page<CustomerDetailResponse> result = customerManagementService.getAllCustomers(filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).getSubscriptionSummary().isHasActiveSubscription());
    }
}
