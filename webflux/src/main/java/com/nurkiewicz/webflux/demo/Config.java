package com.nurkiewicz.webflux.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @Bean
    WebClient webClient() {
        return WebClient
                .builder()
                .codecs(codecs ->
                        codecs.defaultCodecs().maxInMemorySize(1024 * 1024 * 10))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> foo() {
        return route(GET("/router/1/{id}"), this::router1)
                .andRoute(GET("/router/2/{id}"), this::router2)
                .andRoute(GET("/router/cached"), this::routerCached)
                .andRoute(POST("/router"), this::readBody);
    }

    private Mono<ServerResponse> readBody(ServerRequest request) {
        return ServerResponse
                .accepted()
                .body(request
                        .bodyToMono(Person.class)
                        .map(x -> new Confirmation(UUID.randomUUID())), Confirmation.class
                );
    }

    private Mono<ServerResponse> router2(ServerRequest request) {
        return ServerResponse
                .ok()
                .syncBody("Bar " + request.pathVariable("id"));
    }

    private Mono<ServerResponse> routerCached(ServerRequest request) {
        Map<String, String> book = new HashMap<>();
        book.put("title", "Tytuł");
        String version = String.valueOf(book.hashCode());
        return ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .eTag(version) // lastModified is also available
                .syncBody(book);
    }

    private Mono<ServerResponse> router1(ServerRequest request) {
        return ServerResponse
                .ok()
                .syncBody("Foo " + request.pathVariable("id"));
    }

    private static void emitAsync(FluxSink<Object> c, String a) {
        new Thread(() -> {
            log.info("About to publish {}", a);
            c.next(a);
            log.info("Published {}", a);
        }).start();
    }

}


class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}

class Confirmation {
    private final UUID code;

    Confirmation(UUID code) {
        this.code = code;
    }

    public UUID getCode() {
        return code;
    }
}