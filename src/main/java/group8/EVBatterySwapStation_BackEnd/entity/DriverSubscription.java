package group8.EVBatterySwapStation_BackEnd.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import group8.EVBatterySwapStation_BackEnd.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "driver_subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    private int swapsUsed;
    private boolean active;

    @Column(name = "auto_renew")
    private boolean autoRenew;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @OneToOne
    @JoinColumn(name = "battery_id")
    private Battery battery;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Payment payment;
}
