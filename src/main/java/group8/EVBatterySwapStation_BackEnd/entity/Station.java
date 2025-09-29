package group8.EVBatterySwapStation_BackEnd.entity;

import group8.EVBatterySwapStation_BackEnd.enums.StationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long stationId;

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int capacity; // số lượng pin tối đa trạm chứa

    @Enumerated(EnumType.STRING)
    private StationStatus status; // trạng thái hoạt động của trạm

    private String imageUrl; // URL hình ảnh trạm
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Battery> batteries = new ArrayList<>();
}
