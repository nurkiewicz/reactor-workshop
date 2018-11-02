package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Mono;

import java.time.Duration;

public class Ping {

	public static Mono<Boolean> check(String host) {
		return Mono
				.<Boolean>empty()
				.delaySubscription(Duration.ofMillis(10));
	}

}
