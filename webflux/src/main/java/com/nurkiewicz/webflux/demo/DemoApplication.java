package com.nurkiewicz.webflux.demo;

import reactor.core.scheduler.Schedulers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		Schedulers.enableMetrics();
		InitDocker.start();
		SpringApplication.run(DemoApplication.class, args);
	}

}
