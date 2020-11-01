package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

public class ChatHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);

	private final Sinks.Many<String> broadcast = Sinks.many().replay().limit(5);

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		session
				.receive()
				.doOnSubscribe(s -> log.info("Got new subscriber, total: {}", broadcast.currentSubscriberCount() + 1))
				.map(WebSocketMessage::getPayloadAsText)
				.doOnNext(incoming -> log.info("Got message \"{}\", broadcasting to all {} subscribers", incoming, broadcast.currentSubscriberCount()))
				.doOnNext(broadcast::tryEmitNext)
				.doOnTerminate(() -> log.info("Subscriber disconnected, total: {}", broadcast.currentSubscriberCount()))
				.subscribe();
		return session.send(broadcast.asFlux().map(session::textMessage));
	}

}