package group8.EVBatterySwapStation_BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    private String name;

    private String description;

    private Double price; // Giá gói dịch vụ

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "swap_limit")
    private Integer swapLimit;
}
