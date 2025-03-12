package com.demo.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling
@RequestMapping("/api")
public class AIDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(AIDemoApplication.class, args);

	}

}
