package com.team18.FleetForge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FleetForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetForgeApplication.class, args);
	}

}
