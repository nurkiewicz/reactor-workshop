package com.nurkiewicz.webflux.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		InitDocker.start().block(Duration.ofMinutes(2));
		SpringApplication.run(DemoApplication.class, args);
	}


}
