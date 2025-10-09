package group8.EVBatterySwapStation_BackEnd.entity;

import group8.EVBatterySwapStation_BackEnd.enums.BatteryLedgerAction;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "battery_ledger",
        indexes = {
                @Index(name = "idx_ledger_battery_time", columnList = "battery_id,created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battery_id", nullable = false)
    private Battery battery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private BatteryLedgerAction action;

    @Column(name = "ref_swap_id")
    private Long refSwapId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_status")
    private BatteryStatus prevStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private BatteryStatus newStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}



