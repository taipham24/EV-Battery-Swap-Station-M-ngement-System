package group8.SE1840.EVBatterySwapStationManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("group8.SE1840.EVBatterySwapStationManagementSystem.repository")
public class EVBatterySwapStationManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EVBatterySwapStationManagementSystemApplication.class, args);
    }

}
