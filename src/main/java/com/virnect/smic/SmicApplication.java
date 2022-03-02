package com.virnect.smic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SmicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmicApplication.class, args);
	}

}
