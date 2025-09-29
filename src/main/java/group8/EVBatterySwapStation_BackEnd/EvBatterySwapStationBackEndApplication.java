package group8.EVBatterySwapStation_BackEnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("group8.EVBatterySwapStation_BackEnd.repository")
public class EvBatterySwapStationBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvBatterySwapStationBackEndApplication.class, args);
	}

}
