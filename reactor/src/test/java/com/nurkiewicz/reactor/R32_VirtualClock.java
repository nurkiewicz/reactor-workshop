package com.nurkiewicz.reactor;

import java.time.Duration;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofSeconds;
import static reactor.test.StepVerifier.withVirtualTime;

@Ignore
public class R32_VirtualClock {

	private static final Logger log = LoggerFactory.getLogger(R32_VirtualClock.class);

	@Test
	public void virtualTime() throws Exception {
		withVirtualTime(this::longRunning)
				.expectSubscription()
				.expectNoEvent(ofSeconds(2))
				.expectNext("OK")
				.expectComplete()
				.verify(ofSeconds(5));
	}

	/**
	 * TODO Apply {@link Mono#timeout(Duration)} of 1 second to a return value from
	 * {@link #longRunning()} method and verify it works.
	 * Warning: both {@link reactor.test.StepVerifier.LastStep#verifyTimeout(java.time.Duration)} and
	 * {@link reactor.test.StepVerifier.LastStep#expectTimeout(Duration)}
	 * don't verify {@link java.util.concurrent.TimeoutException}
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

}
