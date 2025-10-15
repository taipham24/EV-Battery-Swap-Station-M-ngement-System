package group8.EVBatterySwapStation_BackEnd.entity;

import group8.EVBatterySwapStation_BackEnd.enums.PaymentMethod;
import group8.EVBatterySwapStation_BackEnd.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment",
        indexes = {
                @Index(name = "idx_payment_status_paid", columnList = "status,paid_at"),
                @Index(name = "idx_payment_paid_at", columnList = "paid_at"),
                @Index(name = "idx_payment_method_paid", columnList = "method,paid_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_id")
    private SwapTransaction swap;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Column(name = "amount_vnd", nullable = false)
    private Long amountVnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_staff_id")
    private StaffProfile cashier;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private DriverSubscription subscription;
}
