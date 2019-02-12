package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

public class TimeHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EchoHandler.class);

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final Flux<WebSocketMessage> outMessages = Flux
                .interval(Duration.ofMillis(500))
                .timestamp()
                .map(t -> t.getT2() + "\t" + Instant.ofEpochMilli(t.getT1()))
                .map(session::textMessage)
                .doOnSubscribe(s -> log.info("Got new connection {}", session))
                .doOnComplete(() -> log.info("Connection completed {}", session));
        return session.send(outMessages);
    }

}