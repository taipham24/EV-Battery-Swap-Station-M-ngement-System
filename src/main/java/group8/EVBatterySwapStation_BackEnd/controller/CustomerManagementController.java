package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.CustomerFilterRequest;
import group8.EVBatterySwapStation_BackEnd.DTO.request.UpdateCustomerRequest;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.CustomerDetailResponse;
import group8.EVBatterySwapStation_BackEnd.DTO.response.SubscriptionHistoryResponse;
import group8.EVBatterySwapStation_BackEnd.service.CustomerManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Customer Management", description = "Admin endpoints for customer management")
@PreAuthorize("hasAuthority('ADMIN')")
public class CustomerManagementController {

    private final CustomerManagementService customerManagementService;

    @GetMapping
    @Operation(summary = "Get all customers with filtering and pagination")
    public ResponseEntity<ApiResponse<Page<CustomerDetailResponse>>> getAllCustomers(
            @Parameter(description = "Filter criteria") CustomerFilterRequest filter) {
        log.info("Admin getting all customers with filter: {}", filter);

        Page<CustomerDetailResponse> customers = customerManagementService.getAllCustomers(filter);

        return ResponseEntity.ok(ApiResponse.<Page<CustomerDetailResponse>>builder()
                .code(200)
                .message("Customers retrieved successfully")
                .result(customers)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer detail by ID")
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> getCustomerDetail(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Admin getting customer detail for ID: {}", id);

        CustomerDetailResponse customer = customerManagementService.getCustomerDetail(id);

        return ResponseEntity.ok(ApiResponse.<CustomerDetailResponse>builder()
                .code(200)
                .message("Customer detail retrieved successfully")
                .result(customer)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer information")
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Parameter(description = "Customer update data") @Valid @RequestBody UpdateCustomerRequest request) {
        log.info("Admin updating customer {} with data: {}", id, request);

        CustomerDetailResponse updatedCustomer = customerManagementService.updateCustomer(id, request);

        return ResponseEntity.ok(ApiResponse.<CustomerDetailResponse>builder()
                .code(200)
                .message("Customer updated successfully")
                .result(updatedCustomer)
                .build());
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend customer account")
    public ResponseEntity<ApiResponse<Void>> suspendCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Parameter(description = "Suspension reason") @RequestParam String reason) {
        log.info("Admin suspending customer {} with reason: {}", id, reason);

        customerManagementService.suspendCustomer(id, reason);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Customer suspended successfully")
                .build());
    }

    @PostMapping("/{id}/unsuspend")
    @Operation(summary = "Unsuspend customer account")
    public ResponseEntity<ApiResponse<Void>> unsuspendCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Admin unsuspending customer {}", id);

        customerManagementService.unsuspendCustomer(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Customer unsuspended successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete customer")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Admin soft deleting customer {}", id);

        customerManagementService.deleteCustomer(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Customer deleted successfully")
                .build());
    }

    @GetMapping("/{id}/subscriptions")
    @Operation(summary = "Get customer subscription history")
    public ResponseEntity<ApiResponse<List<SubscriptionHistoryResponse>>> getCustomerSubscriptionHistory(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Admin getting subscription history for customer {}", id);

        List<SubscriptionHistoryResponse> subscriptions = customerManagementService.getCustomerSubscriptionHistory(id);

        return ResponseEntity.ok(ApiResponse.<List<SubscriptionHistoryResponse>>builder()
                .code(200)
                .message("Subscription history retrieved successfully")
                .result(subscriptions)
                .build());
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get customer statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerStatistics(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Admin getting statistics for customer {}", id);

        Map<String, Object> statistics = customerManagementService.getCustomerStatistics(id);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Customer statistics retrieved successfully")
                .result(statistics)
                .build());
    }
}
