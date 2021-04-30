package com.nurkiewicz.webflux.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * TODO
 * <ol>
 *     <li>Use single sink to publish incoming messages</li>
 *     <li>Broadcast that sink to all listening subscribers</li>
 *     <li>New subscriber should receive last 5 messages before joining</li>
 *     <li>Add some logging: connecting/disconnecting, how many subscribers</li>
 * </ol>
 */
public class ChatHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);

	//TODO Use some Sink here

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.send(Flux.empty());
	}

}