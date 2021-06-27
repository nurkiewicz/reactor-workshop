package com.nurkiewicz.reactor.Secrets;

import java.time.Duration;

import reactor.core.publisher.Mono;

public class DontLookHere {

	public static  Mono<Long> operation3() {
		return Mono
				.delay(Duration.ofMillis(50));
	}

	public static  Mono<Long> operation2() {
		return Mono
				.delay(Duration.ofMillis(150));
	}

	public static  Mono<Long> operation1() {
		return Mono
				.delay(Duration.ofMillis(10));
	}

	public static int dangerousMap(int x) {
		return x;
	}

	public static boolean dangerousFilter(int x) {
		if (x == 7) {
			throw new IllegalArgumentException();
		}
		return true;
	}

	public static Mono<Integer> dangerousFlatMap(int x) {
		if (x == 6) {
			return Mono.delay(Duration.ofSeconds(1)).thenReturn(x);
		} else {
			return Mono.just(x);
		}
	}



}
