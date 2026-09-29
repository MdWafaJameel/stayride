package com.stayride.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class StayrideServiceDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(StayrideServiceDiscoveryApplication.class, args);
	}

}
