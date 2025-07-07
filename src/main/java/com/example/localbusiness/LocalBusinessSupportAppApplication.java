package com.example.localbusiness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocalBusinessSupportAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalBusinessSupportAppApplication.class, args);
	}

}
