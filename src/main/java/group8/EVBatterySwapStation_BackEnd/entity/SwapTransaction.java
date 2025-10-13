package group8.EVBatterySwapStation_BackEnd.entity;

import group8.EVBatterySwapStation_BackEnd.enums.SwapStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "swap_transaction",
        indexes = {
                @Index(name = "idx_swap_station_status_created", columnList = "station_id,status,created_at"),
                @Index(name = "idx_swap_driver_created", columnList = "driver_id,created_at"),
                @Index(name = "idx_swap_created_at", columnList = "created_at"),
                @Index(name = "idx_swap_station_created", columnList = "station_id,created_at"),
                @Index(name = "idx_swap_status_created", columnList = "status,created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwapTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swap_id")
    private Long swapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_battery_id", nullable = false)
    private Battery reservedBattery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_battery_id")
    private Battery returnedBattery;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SwapStatus status;

    @Column(name = "amount_vnd")
    private Long amountVnd;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notes", length = 512)
    private String notes;
}




