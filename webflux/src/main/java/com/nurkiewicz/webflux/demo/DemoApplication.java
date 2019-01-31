package com.nurkiewicz.webflux.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testcontainers.containers.GenericContainer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		final CompletableFuture<Integer> mongoPort = startMongo();
		final CompletableFuture<Integer> couchbasePort = start("couchbase:6.0.0", 8091);
		System.setProperty("spring.data.mongodb.port", Integer.toString(mongoPort.get()));
		System.setProperty("spring.couchbase.bootstrap-hosts", "localhost:" + couchbasePort.get());
		SpringApplication.run(DemoApplication.class, args);
	}

	private static CompletableFuture<Integer> start(String containerName, int port) {
		return CompletableFuture.supplyAsync(() -> {
			var container = new GenericContainer(containerName) {
				@Override
				protected void configure() {
					super.configure();
					addExposedPorts(8091, 11207, 11210, 11211, 18091, 18092, 18093);
					addFixedExposedPort(8092, 8092);
					addFixedExposedPort(8093, 8093);
					addFixedExposedPort(8094, 8094);
//					addFixedExposedPort(8095, 8095);
//					setWaitStrategy(new HttpWaitStrategy().forPath("/ui/index.html#/"));
				}
			};
			container.start();
			return Objects.requireNonNull(container.getMappedPort(port), "No mapping for port " + port);
		});
	}
	private static CompletableFuture<Integer> startMongo() {
		return CompletableFuture.supplyAsync(() -> {
			var container = new GenericContainer("mongo:4.0.5");
			container.start();
			return Objects.requireNonNull(container.getMappedPort(27017), "No mapping for port " + 27017);
		});
	}

}
