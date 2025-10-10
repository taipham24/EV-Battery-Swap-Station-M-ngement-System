package group8.EVBatterySwapStation_BackEnd.entity;


import group8.EVBatterySwapStation_BackEnd.enums.BatteryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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

    private String model;
    private String manufacturer;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", unique = true) // unique đảm bảo 1-1
    private Driver driver;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "battery_id", unique = true) // unique đảm bảo 1-1
    private Battery battery; // Pin hiện đang gắn trên xe
}
