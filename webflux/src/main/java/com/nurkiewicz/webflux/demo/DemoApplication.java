package com.nurkiewicz.webflux.demo;

import java.time.Duration;

import reactor.core.scheduler.Schedulers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		Schedulers.enableMetrics();
		InitDocker.start().block(Duration.ofMinutes(2));
		SpringApplication.run(DemoApplication.class, args);
	}

}
