package group8.EVBatterySwapStation_BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "staff_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id",referencedColumnName = "driver_id", unique = true, nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private String workShift;

    // Admin management fields
    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "notes", length = 512)
    private String notes; // admin notes about staff

}
