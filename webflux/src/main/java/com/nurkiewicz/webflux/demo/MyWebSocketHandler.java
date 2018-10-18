package com.nurkiewicz.webflux.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class MyWebSocketHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(MyWebSocketHandler.class);

	@Override
	public Mono<Void> handle(WebSocketSession session) {;
		Flux<WebSocketMessage> outMessages = session
				.receive()
				.doOnSubscribe(s -> log.info("Got new connection"))
				.map(WebSocketMessage::getPayloadAsText)
				.map(String::toUpperCase)
				.doOnNext(x -> log.info("Will send {}", x))
				.map(session::textMessage);
		return session
				.send(outMessages)
				.log();
				//.doOnSuccess(v -> log.info("Done, terminating the connection"));
	}

	public static void main(String[] args) throws URISyntaxException {
		WebSocketClient client = new ReactorNettyWebSocketClient();

		URI url = new URI("ws://localhost:8080/ws");
		client
				.execute(url, session -> {
					Flux<WebSocketMessage> outMessages = Flux
							.interval(Duration.ofSeconds(1))
							.take(10)
							.map(x -> "Message " + x)
							.doOnNext(x -> log.info("About to send {}", x))
							.map(session::textMessage);
					Mono<Void> receiving = session
							.receive()
							.map(WebSocketMessage::getPayloadAsText)
							.doOnNext(x -> log.info("Received {}", x))
							.then();
					return session.send(outMessages).mergeWith(receiving).then();
				})
				.block();
	}
}