package group8.EVBatterySwapStation_BackEnd.controller;

import group8.EVBatterySwapStation_BackEnd.DTO.request.SubscriptionPlanRequest;
import group8.EVBatterySwapStation_BackEnd.entity.SubscriptionPlan;
import group8.EVBatterySwapStation_BackEnd.service.SubscriptionPlanService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {
    private final SubscriptionPlanService subscriptionPlanService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SubscriptionPlan> createSubscription(@RequestBody SubscriptionPlanRequest request) {

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setDurationDays(request.getDurationDays());
        plan.setSwapLimit(request.getSwapLimit());

        System.out.println("DEBUG >>> name=" + plan.getName());
        System.out.println("DEBUG >>> price=" + plan.getPrice());

        SubscriptionPlan createdPlan = subscriptionPlanService.createSubscription(plan);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<SubscriptionPlan>> getPlansSortedByPrice() {
        List<SubscriptionPlan> plans = subscriptionPlanService.getAllPlansSortedByPrice();
        return ResponseEntity.ok(plans);
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SubscriptionPlan> update(@PathVariable Long id, @RequestBody SubscriptionPlanRequest request) {
        SubscriptionPlan updatedPlan = new SubscriptionPlan();
        updatedPlan.setName(request.getName());
        updatedPlan.setDescription(request.getDescription());
        updatedPlan.setPrice(request.getPrice());
        updatedPlan.setDurationDays(request.getDurationDays());
        updatedPlan.setSwapLimit(request.getSwapLimit());
        SubscriptionPlan plan = subscriptionPlanService.update(id, updatedPlan);
        return ResponseEntity.ok(plan);
    }
}
