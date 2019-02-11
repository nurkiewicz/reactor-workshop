package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MyWebSocketHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(MyWebSocketHandler.class);

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<WebSocketMessage> outMessages = session
				.receive()
				.doOnSubscribe(s -> log.info("Got new connection"))
				.map(WebSocketMessage::getPayloadAsText)
				.map(String::toUpperCase)
				.doOnNext(x -> log.info("Will send {}", x))
				.map(session::textMessage);
		return session
				.send(Mono.just(session.textMessage("HELLO!")).concatWith(outMessages))
				.log()
				.doOnSuccess(v -> log.info("Done, terminating the connection"));
	}

}