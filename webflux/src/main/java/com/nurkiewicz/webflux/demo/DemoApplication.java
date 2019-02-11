package com.nurkiewicz.webflux.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		Mono.zip(
				startAsync("mongo:4.0.5", 27017)
						.map(Object::toString)
						.doOnNext(port -> System.setProperty("spring.data.mongodb.port", port)),
				startAsync("redis:5.0.3", 6379)
						.map(Object::toString)
						.doOnNext(port -> System.setProperty("spring.redis.port", port))
				)
				.block();
		SpringApplication.run(DemoApplication.class, args);
	}

	private static Mono<Integer> startAsync(String containerName, int port) {
		return Mono
				.fromCallable(() -> {
					var container = new GenericContainer(containerName);
					container.start();
					return Objects.requireNonNull(container.getMappedPort(port), "No mapping for port " + port);
				})
				.subscribeOn(Schedulers.elastic());
	}

}
