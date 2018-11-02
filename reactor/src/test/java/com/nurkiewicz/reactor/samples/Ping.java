package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Mono;

import java.time.Duration;

public class Ping {

	public static Mono<Void> check(String host) {
		return Mono
				.<Void>empty()
				.delaySubscription(Duration.ofMillis(10));
	}

}
