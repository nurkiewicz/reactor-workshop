package com.nurkiewicz.webflux.demo;

import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

public class InitDocker {

    public static Mono<Void> start() {
        return Mono.zip(
                startAsync("mongo:4.0.5", 27017)
                        .map(Object::toString)
                        .doOnNext(port -> System.setProperty("spring.data.mongodb.port", port)),
                startAsync("redis:5.0.3", 6379)
                        .map(Object::toString)
                        .doOnNext(port -> System.setProperty("spring.redis.port", port))
        ).then();
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
