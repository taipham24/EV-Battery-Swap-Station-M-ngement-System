package group8.EVBatterySwapStation_BackEnd.service;

import group8.EVBatterySwapStation_BackEnd.DTO.request.CustomerFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.UpdateCustomerRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.response.CustomerDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SubscriptionHistoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CustomerManagementService {
    
    /**
     * Get all customers with filtering and pagination
     */
    Page<CustomerDetailResponse> getAllCustomers(CustomerFilterRequest filter);
    
    /**
     * Get customer detail by ID
     */
    CustomerDetailResponse getCustomerDetail(Long customerId);
    
    /**
     * Update customer information
     */
    CustomerDetailResponse updateCustomer(Long customerId, UpdateCustomerRequest request);
    
    /**
     * Suspend customer account
     */
    void suspendCustomer(Long customerId, String reason);
    
    /**
     * Unsuspend customer account
     */
    void unsuspendCustomer(Long customerId);
    
    /**
     * Soft delete customer
     */
    void deleteCustomer(Long customerId);
    
    /**
     * Get customer subscription history
     */
    List<SubscriptionHistoryResponse> getCustomerSubscriptionHistory(Long customerId);
    
    /**
     * Get customer statistics
     */
    Map<String, Object> getCustomerStatistics(Long customerId);
}
