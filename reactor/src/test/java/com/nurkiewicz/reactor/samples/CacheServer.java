package com.nurkiewicz.reactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class CacheServer {

	private static final Logger log = LoggerFactory.getLogger(CacheServer.class);

	private final String host;
	private final Duration delay;
	private final double failureProbability;

	public CacheServer(String host, Duration delay, double failureProbability) {
		this.host = host;
		this.delay = delay;
		this.failureProbability = failureProbability;
	}

	public Mono<String> findBy(int id) {
		return Mono.defer(() -> {
			final double jitter = ThreadLocalRandom.current().nextGaussian() * delay.toMillis() / 10;
			return Mono
					.fromCallable(() -> {
						if (ThreadLocalRandom.current().nextDouble() < failureProbability) {
							throw new IOException("Simulated fault");
						}
						return "Value-" + id + " from " + host;
					})
					.delayElement(delay.plus(Duration.ofMillis((long) jitter)));
		});
	}

}
