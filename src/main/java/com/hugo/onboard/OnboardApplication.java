package com.hugo.onboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnboardApplication.class, args);
	}

}
