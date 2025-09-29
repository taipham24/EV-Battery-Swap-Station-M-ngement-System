package group8.EVBatterySwapStation_BackEnd.configuration;

import group8.EVBatterySwapStation_BackEnd.entity.Driver;
import group8.EVBatterySwapStation_BackEnd.entity.RoleDetail;
import group8.EVBatterySwapStation_BackEnd.enums.Role;
import group8.EVBatterySwapStation_BackEnd.repository.DriverRepository;
import group8.EVBatterySwapStation_BackEnd.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(DriverRepository driverRepository, RoleRepository roleRepository) {
        return args -> {
            if (driverRepository.findByUserName("admin").isEmpty()) {
                Driver driver = new Driver();
                var roles = new HashSet<RoleDetail>();

                //Save userRoleType to UserRole Entity
                RoleDetail userRole = new RoleDetail();
                userRole.setRoleType(Role.ADMIN.name());
                roleRepository.save(userRole);
                roles.add(userRole);

                //create admin acc
                driver.setUserName("admin");
                driver.setPassword(passwordEncoder.encode("admin"));
                driver.setRoles(roles);
                driver.setStatus(true);
                driverRepository.save(driver);
                log.warn("admin user has been created!");
            }
        };
    }
}
