package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EchoHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(EchoHandler.class);

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<WebSocketMessage> outMessages = session
				.receive()
				.doOnSubscribe(s -> log.info("Got new connection"))
				.map(WebSocketMessage::getPayloadAsText)
				.doOnNext(x -> log.info("Recevied: '{};", x))
				.map(String::toUpperCase)
				.doOnNext(x -> log.info("Sending {}", x))
				.map(session::textMessage);
		return session
				.send(outMessages)
				.doOnSuccess(v -> log.info("Done, terminating the connection"));
	}

}