package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;

import static java.time.Duration.*;
import static reactor.test.StepVerifier.withVirtualTime;

@Ignore
public class R070_VirtualClock {

	private static final Logger log = LoggerFactory.getLogger(R070_VirtualClock.class);

	@Test
	public void virtualTime() throws Exception {
		withVirtualTime(this::longRunning)
				.expectSubscription()
				.expectNoEvent(ofSeconds(2))
				.expectNext("OK")
				.verifyComplete();
	}

	/**
	 * TODO Apply {@link Mono#timeout(Duration)} of 1 second to a return value from {@link #longRunning()} method and verify it works
	 */
	@Test
	public void timeout() throws Exception {
		//TODO Write whole test :-)
	}

	Mono<String> longRunning() {
		return Mono
				.delay(Duration.ofMillis(2000))
				.map(x -> "OK");
	}

	@Test
	public void should() throws Exception {
		//given
		final VirtualTimeScheduler time = VirtualTimeScheduler.create();
		final Flux<Long> slow = Flux.interval(ofDays(1), time);
		final Flux<Long> fast = Flux.interval(ofHours(6), time);

		//when
		final Flux<Tuple2<Long, Long>> ticks = Flux.combineLatest(fast, slow, Tuples::of);

		//then
		final StepVerifier.FirstStep<Tuple2<Long, Long>> sv = StepVerifier.create(ticks);
		time.advanceTimeBy(ofHours(6));
		sv.expectNext(Tuples.of(0L, 0L));
	}

}
