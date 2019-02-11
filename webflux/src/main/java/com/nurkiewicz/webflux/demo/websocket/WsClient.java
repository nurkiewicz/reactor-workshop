package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class WsClient {

    private static final Logger log = LoggerFactory.getLogger(WsClient.class);

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        WebSocketClient client = new ReactorNettyWebSocketClient();

        URI url = new URI("ws://localhost:8080/time");
        final Mono<Void> reqResp = client
                .execute(url, session -> {
                    Flux<WebSocketMessage> outMessages = Flux
                            .interval(Duration.ofSeconds(1))
                            .take(10)
                            .map(x -> "Message " + x)
                            .doOnNext(x -> log.info("About to send '{}'", x))
                            .map(session::textMessage);
                    Mono<Void> receiving = session
                            .receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .doOnNext(x -> log.info("Received '{}'", x))
                            .then();
                    return session.send(outMessages).mergeWith(receiving).then();
                });

        final Disposable disposable = reqResp.subscribe();
        TimeUnit.SECONDS.sleep(20);
        disposable.dispose();
    }

}
