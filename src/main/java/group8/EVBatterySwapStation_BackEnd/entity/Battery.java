package group8.EVBatterySwapStation_BackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group8.EVBatterySwapStation_BackEnd.enums.BatteryStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battery",
        indexes = {
                @Index(name = "idx_battery_station_status", columnList = "station_id,status"),
                @Index(name = "idx_battery_model", columnList = "model")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "battery_id")
    private Long batteryId;

    @Column(name = "serial_number", unique = true, nullable = false, length = 64)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    @JsonIgnore
    private Station station;

    @Enumerated(EnumType.STRING)
    private BatteryStatus status;

    @Column(name = "capacity_wh", nullable = false)
    private Integer capacityWh;

    @Column(name = "model", nullable = false, length = 128)
    private String model;
}
