package SmartFoodStreet_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartFoodStreetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFoodStreetApplication.class, args);
    }

}