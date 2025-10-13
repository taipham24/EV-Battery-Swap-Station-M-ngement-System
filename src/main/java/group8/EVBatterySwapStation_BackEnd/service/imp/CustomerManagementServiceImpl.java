package group8.EVBatterySwapStation_BackEnd.service.imp;

import group8.EVBatterySwapStation_BackEnd.DTO.request.CustomerFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.UpdateCustomerRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.CustomerDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SubscriptionHistoryResponse;
import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.RoleDetail;
import group8.EVBatterySwapStation_BackEnd.entity.SwapTransaction;
import group8.EVBatterySwapStation_BackEnd.enums.Role;
import group8.EVBatterySwapStation_BackEnd.exception.AppException;
import group8.EVBatterySwapStation_BackEnd.exception.ErrorCode;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.DriverSubscriptionRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import group8.EVBatterySwapStation_BackEnd.repository.SwapTransactionRepository;
import group8.EVBatterySwapStation_BackEnd.service.CustomerManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerManagementServiceImpl implements CustomerManagementService {

    private final DriverRepository driverRepository;
    private final DriverSubscriptionRepository driverSubscriptionRepository;
    private final SwapTransactionRepository swapTransactionRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<CustomerDetailResponse> getAllCustomers(CustomerFilterRequest filter) {
        log.info("Getting customers with filter: {}", filter);
        
        // Create pageable
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDirection()) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), 
            Sort.by(direction, filter.getSortBy()));
        
        // Get DRIVER role
        RoleDetail driverRole = roleRepository.findByRoleType(Role.DRIVER.name())
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        
        Page<Driver> drivers;
        
        if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            drivers = driverRepository.searchCustomers(filter.getKeyword().trim(), pageable);
        } else {
            drivers = driverRepository.findByDeletedFalseAndRolesContaining(driverRole, pageable);
        }
        
        return drivers.map(this::mapToCustomerDetailResponse);
    }

    @Override
    public CustomerDetailResponse getCustomerDetail(Long customerId) {
        log.info("Getting customer detail for ID: {}", customerId);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        // Verify this is a customer (has DRIVER role)
        boolean isCustomer = driver.getRoles().stream()
            .anyMatch(role -> Role.DRIVER.name().equals(role.getRoleType()));
        
        if (!isCustomer) {
            throw new AppException(ErrorCode.DRIVER_NOT_FOUND);
        }
        
        return mapToCustomerDetailResponse(driver);
    }

    @Override
    @Transactional
    public CustomerDetailResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        log.info("Updating customer {} with data: {}", customerId, request);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        // Verify this is a customer
        boolean isCustomer = driver.getRoles().stream()
            .anyMatch(role -> Role.DRIVER.name().equals(role.getRoleType()));
        
        if (!isCustomer) {
            throw new AppException(ErrorCode.DRIVER_NOT_FOUND);
        }
        
        // Update fields
        if (request.getUserName() != null) {
            driver.setUserName(request.getUserName());
        }
        if (request.getEmail() != null) {
            driver.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            driver.setFullName(request.getFullName());
        }
        if (request.getStatus() != null) {
            driver.setStatus(request.getStatus());
        }
        
        driver.setUpdatedAt(Instant.now());
        Driver savedDriver = driverRepository.save(driver);
        
        log.info("Customer {} updated successfully", customerId);
        return mapToCustomerDetailResponse(savedDriver);
    }

    @Override
    @Transactional
    public void suspendCustomer(Long customerId, String reason) {
        log.info("Suspending customer {} with reason: {}", customerId, reason);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        driver.setSuspended(true);
        driver.setSuspensionReason(reason);
        driver.setUpdatedAt(Instant.now());
        
        driverRepository.save(driver);
        log.info("Customer {} suspended successfully", customerId);
    }

    @Override
    @Transactional
    public void unsuspendCustomer(Long customerId) {
        log.info("Unsuspending customer {}", customerId);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        driver.setSuspended(false);
        driver.setSuspensionReason(null);
        driver.setUpdatedAt(Instant.now());
        
        driverRepository.save(driver);
        log.info("Customer {} unsuspended successfully", customerId);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("Soft deleting customer {}", customerId);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        driver.setDeleted(true);
        driver.setUpdatedAt(Instant.now());
        
        driverRepository.save(driver);
        log.info("Customer {} soft deleted successfully", customerId);
    }

    @Override
    public List<SubscriptionHistoryResponse> getCustomerSubscriptionHistory(Long customerId) {
        log.info("Getting subscription history for customer {}", customerId);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        List<DriverSubscription> subscriptions = driverSubscriptionRepository
            .findByDriverIdOrderByStartDateDesc(customerId);
        
        return subscriptions.stream()
            .map(this::mapToSubscriptionHistoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getCustomerStatistics(Long customerId) {
        log.info("Getting statistics for customer {}", customerId);
        
        Driver driver = driverRepository.findByDriverIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.DRIVER_NOT_FOUND));
        
        // Get swap statistics
        List<SwapTransaction> swaps = swapTransactionRepository.findByDriver_DriverId(customerId, Pageable.unpaged())
            .getContent();
        
        long totalSwaps = swaps.size();
        long totalRevenue = swaps.stream()
            .filter(swap -> swap.getAmountVnd() != null)
            .mapToLong(SwapTransaction::getAmountVnd)
            .sum();
        
        // Get subscription statistics
        List<DriverSubscription> subscriptions = driverSubscriptionRepository
            .findByDriverIdOrderByStartDateDesc(customerId);
        
        long totalSubscriptions = subscriptions.size();
        long activeSubscriptions = subscriptions.stream()
            .mapToLong(sub -> sub.isActive() ? 1 : 0)
            .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSwaps", totalSwaps);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalSubscriptions", totalSubscriptions);
        stats.put("activeSubscriptions", activeSubscriptions);
        stats.put("accountCreated", driver.getCreatedAt());
        stats.put("lastLogin", driver.getLastLogin());
        stats.put("isSuspended", driver.isSuspended());
        
        return stats;
    }

    private CustomerDetailResponse mapToCustomerDetailResponse(Driver driver) {
        // Get active subscription
        Optional<DriverSubscription> activeSubscription = driverSubscriptionRepository
            .findByDriverAndActiveTrue(driver);
        
        CustomerDetailResponse.SubscriptionSummary subscriptionSummary = null;
        if (activeSubscription.isPresent()) {
            DriverSubscription sub = activeSubscription.get();
            subscriptionSummary = CustomerDetailResponse.SubscriptionSummary.builder()
                .subscriptionId(sub.getSubscriptionId())
                .planName(sub.getPlan().getName())
                .batterySerial(sub.getBattery().getSerialNumber())
                .swapsUsed(sub.getSwapsUsed())
                .swapLimit(sub.getPlan().getSwapLimit())
                .swapsRemaining(Math.max(0, sub.getPlan().getSwapLimit() - sub.getSwapsUsed()))
                .active(sub.isActive())
                .hasActiveSubscription(sub.isActive())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .autoRenew(sub.isAutoRenew())
                .build();
        }
        
        // Get swap statistics
        List<SwapTransaction> swaps = swapTransactionRepository.findByDriver_DriverId(driver.getDriverId(), Pageable.unpaged())
            .getContent();
        
        long totalSwaps = swaps.size();
        long totalRevenue = swaps.stream()
            .filter(swap -> swap.getAmountVnd() != null)
            .mapToLong(SwapTransaction::getAmountVnd)
            .sum();
        
        return CustomerDetailResponse.builder()
            .driverId(driver.getDriverId())
            .userName(driver.getUserName())
            .email(driver.getEmail())
            .fullName(driver.getFullName())
            .status(driver.isStatus())
            .suspended(driver.isSuspended())
            .suspensionReason(driver.getSuspensionReason())
            .createdAt(driver.getCreatedAt() != null ? driver.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
            .updatedAt(driver.getUpdatedAt() != null ? driver.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
            .lastLogin(driver.getLastLogin())
            .subscriptionSummary(subscriptionSummary)
            .totalSwaps(totalSwaps)
            .totalRevenue(totalRevenue)
            .build();
    }

    private SubscriptionHistoryResponse mapToSubscriptionHistoryResponse(DriverSubscription subscription) {
        return SubscriptionHistoryResponse.builder()
            .subscriptionId(subscription.getSubscriptionId())
            .planName(subscription.getPlan().getName())
            .batterySerial(subscription.getBattery().getSerialNumber())
            .startDate(subscription.getStartDate())
            .endDate(subscription.getEndDate())
            .swapsUsed(subscription.getSwapsUsed())
            .swapLimit(subscription.getPlan().getSwapLimit())
            .active(subscription.isActive())
            .autoRenew(subscription.isAutoRenew())
            .build();
    }
}
