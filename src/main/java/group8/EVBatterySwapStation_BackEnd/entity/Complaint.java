package group8.EVBatterySwapStation_BackEnd.entity;

import group8.EVBatterySwapStation_BackEnd.enums.ComplaintStatus;
import group8.EVBatterySwapStation_BackEnd.enums.ComplaintType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint",
        indexes = {
                @Index(name = "idx_complaint_status", columnList = "status"),
                @Index(name = "idx_complaint_driver", columnList = "driver_id"),
                @Index(name = "idx_complaint_type", columnList = "type"),
                @Index(name = "idx_complaint_created", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long complaintId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_id")
    private SwapTransaction swap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battery_id")
    private Battery battery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ComplaintType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplaintStatus status;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_staff_id")
    private StaffProfile reviewedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
