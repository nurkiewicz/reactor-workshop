package com.nurkiewicz.webflux.demo;

import com.google.common.net.HostAndPort;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

public class InitDocker {

    public static Mono<Void> start() {
        return Mono.zip(
                startAsync("mongo:4.0.5", 27017)
                        .doOnNext(addr -> configure(addr, "spring.data.mongodb")),
                startAsync("redis:5.0.3", 6379)
                        .doOnNext(addr -> configure(addr, "spring.redis"))
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
                    container.withReuse(true).start();
                    return HostAndPort.fromParts(
                            container.getContainerIpAddress(),
                            Objects.requireNonNull(container.getMappedPort(port), "No mapping for port " + port)
                    );
                })
                .subscribeOn(Schedulers.elastic());
    }

}
