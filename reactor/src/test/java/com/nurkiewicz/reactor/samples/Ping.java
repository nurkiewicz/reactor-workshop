package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofMillis;

public class Ping {

	public static Mono<Boolean> check(String host) {
		return Mono
				.<Boolean>empty()
				.delaySubscription(ofMillis(10));
	}

	public static Flux<Boolean> checkConstantly(String host) {
		switch (host) {
			case "buggy.com":
				return Flux.just(true, true, true, false, true, false, false, true, true, false, false, false).repeat();
			case "vary.com":
				return Flux.concat(
						Flux.just(true, true, true)
								.delayElements(ofMillis(10)),
						Flux.just(true)
								.delayElements(ofMillis(200)),  // 3, 11, 19, 27
						Flux.just(true, true, true)
								.delayElements(ofMillis(10)),
						Flux.just(true)
								.delayElements(ofMillis(50))
				).repeat();
			default:
				return Flux.just(true, true, false, false).repeat();
		}
	}

}
