package com.nurkiewicz.webflux.demo;

import java.util.Objects;

import com.google.common.net.HostAndPort;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class InitDocker {

    public static Mono<Void> start() {
        return Mono.zip(
                startAsync("mongo:4.0.5", 27017)
                        .doOnNext(addr -> configure(addr, "spring.data.mongodb")),
                startAsync("redis:5.0.3", 6379)
                        .doOnNext(addr -> configure(addr, "spring.redis")),
                startAsync("postgres:13.1", 5432)
                        .doOnNext(addr ->
                                System.setProperty("spring.r2dbc.url", "r2dbc:postgresql://" + addr.getHost() + ":" + addr.getPort() + "/test")
                        )
        ).then();
    }

    private static void configure(HostAndPort addr, String configPrefix) {
        System.setProperty(configPrefix + ".host", addr.getHost());
        System.setProperty(configPrefix + ".port", String.valueOf(addr.getPort()));

    }

    private static Mono<HostAndPort> startAsync(String containerName, int port) {
        return Mono
                .fromCallable(() -> {
                    var container = new GenericContainer(containerName);
                    container.addEnv("POSTGRES_DB", "test");
                    container.addEnv("POSTGRES_USER", "test");
                    container.addEnv("POSTGRES_PASSWORD", "test");
                    container.addExposedPort(port);
                    container.withReuse(true).start();
                    return HostAndPort.fromParts(
                            container.getContainerIpAddress(),
                            Objects.requireNonNull(container.getMappedPort(port), "No mapping for port " + port)
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

}
