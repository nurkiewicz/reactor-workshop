package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Ping {

	public static Mono<Boolean> check(String host) {
		return Mono
				.<Boolean>empty()
				.delaySubscription(Duration.ofMillis(10));
	}

	public static Flux<Boolean> checkConstantly(String host) {
		switch (host) {
			case "buggy.com":
				return Flux.just(true, true, true, false, true, false, false, true, true, false, false, false).repeat();
			default:
				return Flux.just(true, true, false, false).repeat();
		}
	}

}
