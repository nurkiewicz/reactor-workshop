package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.samples.CacheServer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class R050_SubscribeOnPublishOn {

	private static final Logger log = LoggerFactory.getLogger(R050_SubscribeOnPublishOn.class);

	private final CacheServer reliable = new CacheServer("foo", Duration.ofMillis(1_000), 0);

	@Test
	public void sameThread() throws Exception {
		final Mono<String> one = Mono.fromCallable(() -> reliable.findBlocking(41));
		final Mono<String> two = Mono.fromCallable(() -> reliable.findBlocking(42));

		log.info("Starting");
		one.subscribe(x -> log.info("Got from one: {}", x));
		log.info("Got first response");
		two.subscribe(x -> log.info("Got from two: {}", x));
		log.info("Got second response");
	}

	@Test
	public void subscribeOn() throws Exception {
		Mono
				.fromCallable(() -> reliable.findBlocking(41))
				.subscribeOn(Schedulers.elastic())
				.doOnNext(x -> log.info("Received {}", x))
				.map(x -> {
					log.info("Mapping {}", x);
					return x;
				})
				.filter(x -> {
					log.info("Filtering {}", x);
					return true;
				})
				.doOnNext(x -> log.info("Still here {}", x))
				.subscribe(x -> log.info("Finally received {}", x));

		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void manyStreamsButLastThreadWins() throws Exception {
		final Mono<String> one = Mono.fromCallable(() -> reliable.findBlocking(41));
		final Mono<String> two = Mono.fromCallable(() -> reliable.findBlocking(42));

		Mono
				.zip(
						one.subscribeOn(Schedulers.elastic()),
						two.subscribeOn(Schedulers.elastic())
				)
				.doOnNext(x -> log.info("Received {}", x))
				.map(x -> {
					log.info("Mapping {}", x);
					return x;
				})
				.filter(x -> {
					log.info("Filtering {}", x);
					return true;
				})
				.doOnNext(x -> log.info("Still here {}", x))
				.subscribe(x -> log.info("Finally received {}", x));

		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void publishOn() throws Exception {
		Mono
				.fromCallable(() -> reliable.findBlocking(41))
				.subscribeOn(Schedulers.newElastic("Ela1"))
				.doOnNext(x -> log.info("Received {}", x))
				.publishOn(Schedulers.newElastic("Ela2"))
				.map(x -> {
					log.info("Mapping {}", x);
					return x;
				})
				.publishOn(Schedulers.newElastic("Ela3"))
				.filter(x -> {
					log.info("Filtering {}", x);
					return true;
				})
				.publishOn(Schedulers.newElastic("Ela4"))
				.doOnNext(x -> log.info("Still here {}", x))
				.publishOn(Schedulers.newElastic("Ela5"))
				.subscribe(x -> log.info("Finally received {}", x));

		TimeUnit.SECONDS.sleep(2);
	}

}
