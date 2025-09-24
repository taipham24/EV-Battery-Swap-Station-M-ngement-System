package group8.SE1840.EVBatterySwapStationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vin", unique = true, nullable = false, length = 50)
    private String vin;

    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "manufacture_year", nullable = false)
    private int manufactureYear;   // năm sản xuất hoặc năm đưa vào sử dụng

    @Column(name = "battery_type", nullable = false, length = 100)
    private String batteryType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", unique = true) // unique đảm bảo 1-1
    private Driver driver;
}
