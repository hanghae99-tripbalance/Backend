package com.move.TripBalance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TripbalanceApplication {

    public static void main(String[] args) {

        SpringApplication.run(TripbalanceApplication.class, args);
        System.out.println("실행");
    }
}
