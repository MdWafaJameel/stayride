package com.stayride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StayrideApplication {

	public static void main(String[] args) {
		SpringApplication.run(StayrideApplication.class, args);
	}

}
