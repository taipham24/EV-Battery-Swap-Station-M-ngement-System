package group8.EVBatterySwapStation_BackEnd.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @NotBlank(message = "Package name is required")
    @Size(max = 100, message = "Package name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Min(value = 0, message = "Price must be non-negative")
    @Column(name = "price", nullable = false)
    private Long price; // Giá gói dịch vụ

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "swap_limit")
    private Integer swapLimit;

    @Column(name = "price_per_swap")
    private Double pricePerSwap; // giá khi KHÔNG có gói thuê (per-swap)

    @Column(name = "price_per_extra_swap")
    private Double pricePerExtraSwap; // giá khi vượt giới hạn trong gói

    // Admin management fields
    @Column(name = "active")
    private boolean active = true; // enable/disable plan

    @Column(name = "display_order")
    private Integer displayOrder = 0; // for sorting in UI

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // JSON string of features
}
