package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class CacheServer {

	private final String host;
	private final Duration delay;
	private final double failureProbability;

	public CacheServer(String host, Duration delay, double failureProbability) {
		this.host = host;
		this.delay = delay;
		this.failureProbability = failureProbability;
	}

	public Mono<String> findBy(int id) {
		final double jitter = ThreadLocalRandom.current().nextGaussian() * delay.toMillis() / 10;
		if (ThreadLocalRandom.current().nextDouble() < failureProbability) {
			return Mono
					.<String>error(new IOException("Simulated fault"))
					.delayElement(delay.plus(Duration.ofMillis((long) jitter)));
		}
		return Mono
				.just("Value-" + id + " from " + host)
				.delayElement(delay.plus(Duration.ofMillis((long) jitter)));
	}

}
