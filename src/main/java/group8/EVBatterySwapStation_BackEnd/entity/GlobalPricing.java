package group8.EVBatterySwapStation_BackEnd.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "global_pricing")
public class GlobalPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price_per_swap", nullable = false)
    private Double pricePerSwap; // giá cho mỗi lượt đổi pin
}
