package group8.EVBatterySwapStation_BackEnd.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "swap_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwapTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id", nullable = true)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "old_battery_id", referencedColumnName = "battery_id")
    private Battery oldBattery;

    @ManyToOne
    @JoinColumn(name = "new_battery_id", referencedColumnName = "battery_id")
    private Battery newBattery;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "station_id", referencedColumnName = "station_id")
    private Station station;

    @Column(name = "swap_time")
    private LocalDateTime swapTime;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id", nullable = false)
    private Payment payment;
}
