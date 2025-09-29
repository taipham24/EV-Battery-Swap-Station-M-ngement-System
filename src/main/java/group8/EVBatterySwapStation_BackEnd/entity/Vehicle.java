package group8.EVBatterySwapStation_BackEnd.entity;


import group8.EVBatterySwapStation_BackEnd.enums.BatteryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vin", unique = true, nullable = false, length = 17)
    private String vin;

    @Enumerated(EnumType.STRING)
    private BatteryType batteryType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", unique = true) // unique đảm bảo 1-1
    private Driver driver;

}
