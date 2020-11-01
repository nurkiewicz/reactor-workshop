package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

public class ChatHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);

	//TODO Use some Sink here

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.send(Flux.empty());
	}

}