package group8.EVBatterySwapStation_BackEnd.entity;

import group8.EVBatterySwapStation_BackEnd.enums.InspectionCondition;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "battery_inspection",
        indexes = {
                @Index(name = "idx_inspection_battery_time", columnList = "battery_id,inspected_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id")
    private Long inspectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_id", nullable = false)
    private SwapTransaction swap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battery_id", nullable = false)
    private Battery battery;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    private InspectionCondition condition;

    @Column(name = "soc_percent", nullable = false)
    private Integer socPercent;

    @Column(name = "notes", length = 512)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_staff_id", nullable = false)
    private StaffProfile inspector;

    @Column(name = "inspected_at", nullable = false)
    private LocalDateTime inspectedAt;
}




