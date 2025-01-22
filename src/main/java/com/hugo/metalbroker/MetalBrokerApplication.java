package com.hugo.metalbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MetalBrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetalBrokerApplication.class, args);
	}

}
