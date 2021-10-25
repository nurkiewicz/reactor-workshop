package com.nurkiewicz.reactor.samples;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.time.Duration.ofMillis;

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
		return Mono.defer(() ->
				Mono
						.fromCallable(() -> findInternal(id))
						.subscribeOn(Schedulers.boundedElastic())
						.doOnSubscribe(s -> log.debug("Fetching {} from {}", id, host))
						.doOnNext(value -> log.debug("Fetched {} from {}", id, host)));
	}

	public String findBlocking(int id) {
		log.debug("Fetching {} from {}", id, host);
		final String value = findInternal(id);
		log.debug("Returning {} from {}", value, host);
		return value;
	}

	private String findInternal(int id) {
		final double jitter = ThreadLocalRandom.current().nextGaussian() * delay.toMillis() / 10;
		Sleeper.sleepRandomly(delay.plus(ofMillis((int) jitter)));
		if (ThreadLocalRandom.current().nextDouble() < failureProbability) {
			throw new IllegalStateException("Simulated fault");
		}
		return "Value-" + id + " from " + host;
	}

}
