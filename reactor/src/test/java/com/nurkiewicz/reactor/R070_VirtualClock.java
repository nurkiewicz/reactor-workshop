package com.nurkiewicz.reactor;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofSeconds;
import static reactor.test.StepVerifier.withVirtualTime;

public class R070_VirtualClock {

	private static final Logger log = LoggerFactory.getLogger(R070_VirtualClock.class);

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
	 * TODO Apply {@link Mono#timeout(Duration)} of 1 second to a return value from {@link #longRunning()} method and verify it works.
	 * Warning: {@link reactor.test.StepVerifier.LastStep#verifyTimeout(java.time.Duration)} doesn't verify {@link java.util.concurrent.TimeoutException}
	 */
	@Test
	public void timeout() throws Exception {
		withVirtualTime(this::longRunningWithTimeout)
				.expectSubscription()
				.expectNoEvent(ofSeconds(1))
				.verifyError(TimeoutException.class);
	}

	Mono<String> longRunningWithTimeout() {
		return longRunning()
				.timeout(ofSeconds(1));
	}

	Mono<String> longRunning() {
		return Mono
				.delay(Duration.ofMillis(2000))
				.map(x -> "OK");
	}

}
