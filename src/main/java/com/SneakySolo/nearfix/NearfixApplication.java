package com.SneakySolo.nearfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NearfixApplication {

	public static void main(String[] args) {
        SpringApplication.run(NearfixApplication.class, args);
	}

}
