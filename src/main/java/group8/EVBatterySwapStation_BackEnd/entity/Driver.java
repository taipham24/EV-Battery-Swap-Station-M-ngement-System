package group8.EVBatterySwapStation_BackEnd.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255)")
    private String userName;

    private String password;

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    private String fullName;
    private boolean status;
    @Column(name = "created_at", nullable = true, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = true)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "driver_roles",
            joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleDetail> roles;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Vehicle vehicle;
    // Dịch vụ đổi pin
    @Column(name = "is_subscribed")
    private boolean isSubscribed; // true nếu đã đăng ký dịch vụ đổi pin
}
