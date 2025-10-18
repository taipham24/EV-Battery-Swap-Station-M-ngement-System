package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SubscriptionPlanRequest;
import group8.EVBatterySwapStation_BackEnd.entity.ApiResponse;
import group8.EVBatterySwapStation_BackEnd.entity.DriverSubscription;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import group8.EVBatterySwapStation_BackEnd.service.SubscriptionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription-plans")
@Slf4j
@Tag(name = "Subscription Plan Management", description = "Endpoints for subscription plan management")
public class SubscriptionPlanController {
    private final SubscriptionPlanService subscriptionPlanService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new subscription plan")
    public ResponseEntity<ApiResponse<SubscriptionPlan>> createSubscription(
            @Parameter(description = "Subscription plan data") @Valid @RequestBody SubscriptionPlanRequest request) {
        log.info("Admin creating subscription plan: {}", request.getName());

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice((long) request.getPrice());
        plan.setDurationDays(request.getDurationDays());
        plan.setSwapLimit(request.getSwapLimit());
        plan.setPricePerSwap(request.getPricePerSwap());
        plan.setPricePerExtraSwap(request.getPricePerExtraSwap());
        plan.setActive(true);
        plan.setDisplayOrder(0);

        SubscriptionPlan createdPlan = subscriptionPlanService.createSubscription(plan);

        return ResponseEntity.ok(ApiResponse.<SubscriptionPlan>builder()
                .code(200)
                .message("Subscription plan created successfully")
                .result(createdPlan)
                .build());
    }

    @GetMapping("/sorted")
    @Operation(summary = "Get all active subscription plans sorted by price")
    public ResponseEntity<ApiResponse<List<SubscriptionPlan>>> getPlansSortedByPrice() {
        log.info("Getting subscription plans sorted by price");

        List<SubscriptionPlan> plans = subscriptionPlanService.getAllPlansSortedByPrice();

        return ResponseEntity.ok(ApiResponse.<List<SubscriptionPlan>>builder()
                .code(200)
                .message("Subscription plans retrieved successfully")
                .result(plans)
                .build());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all subscription plans (including inactive)")
    public ResponseEntity<ApiResponse<List<SubscriptionPlan>>> getAllPlans() {
        log.info("Admin getting all subscription plans");

        List<SubscriptionPlan> plans = subscriptionPlanService.getAllPlans();

        return ResponseEntity.ok(ApiResponse.<List<SubscriptionPlan>>builder()
                .code(200)
                .message("All subscription plans retrieved successfully")
                .result(plans)
                .build());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update subscription plan")
    public ResponseEntity<ApiResponse<SubscriptionPlan>> update(
            @Parameter(description = "Plan ID") @PathVariable Long id,
            @Parameter(description = "Updated plan data") @Valid @RequestBody SubscriptionPlanRequest request) {
        log.info("Admin updating subscription plan {} with data: {}", id, request);

        SubscriptionPlan updatedPlan = new SubscriptionPlan();
        updatedPlan.setName(request.getName());
        updatedPlan.setDescription(request.getDescription());
        updatedPlan.setPrice((long) request.getPrice());
        updatedPlan.setDurationDays(request.getDurationDays());
        updatedPlan.setSwapLimit(request.getSwapLimit());
        updatedPlan.setPricePerSwap(request.getPricePerSwap());
        updatedPlan.setPricePerExtraSwap(request.getPricePerExtraSwap());

        SubscriptionPlan plan = subscriptionPlanService.update(id, updatedPlan);

        return ResponseEntity.ok(ApiResponse.<SubscriptionPlan>builder()
                .code(200)
                .message("Subscription plan updated successfully")
                .result(plan)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Deactivate subscription plan")
    public ResponseEntity<ApiResponse<Void>> deactivatePlan(
            @Parameter(description = "Plan ID") @PathVariable Long id) {
        log.info("Admin deactivating subscription plan {}", id);

        subscriptionPlanService.deactivatePlan(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Subscription plan deactivated successfully")
                .build());
    }

    @GetMapping("/{id}/subscribers")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get subscribers of a specific plan")
    public ResponseEntity<ApiResponse<Page<DriverSubscription>>> getPlanSubscribers(
            @Parameter(description = "Plan ID") @PathVariable Long id,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.info("Admin getting subscribers for plan {}", id);

        Page<DriverSubscription> subscribers = subscriptionPlanService.getPlanSubscribers(id, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<DriverSubscription>>builder()
                .code(200)
                .message("Plan subscribers retrieved successfully")
                .result(subscribers)
                .build());
    }

    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get subscription plan statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlanStatistics(
            @Parameter(description = "Plan ID") @PathVariable Long id) {
        log.info("Admin getting statistics for plan {}", id);

        Map<String, Object> statistics = subscriptionPlanService.getPlanStatistics(id);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Plan statistics retrieved successfully")
                .result(statistics)
                .build());
    }
}
