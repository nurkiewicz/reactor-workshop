package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.samples.CacheServer;
import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Function;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

@Ignore
public class R046_Timeout {

	private static final Logger log = LoggerFactory.getLogger(R046_Timeout.class);

	/**
	 * TODO Ask two {@link CacheServer}s for the same key 1.
	 * <p>
	 *     Ask <code>first</code> server in the beginning.
	 *     If it doesn't respond within 200 ms, continue waiting, but ask <code>second</code> server.
	 *     Second server is much faster, but fails often. If it fails, swallow the exception and wait
	 *     for the first server anyway.
	 *     However if the second server doesn't fail, you'll get the response faster.
	 *     Make sure to run the test a few times to make sure it works on both branches.
	 * </p>
	 *
	 * @see Flux#merge(Publisher[])
	 * @see Mono#delaySubscription(Duration)
	 * @see Mono#onErrorResume(Function)
	 */
	@Test
	public void speculativeExecution() throws Exception {
		//given
		CacheServer first = new CacheServer("foo", ofSeconds(1), 0);
		CacheServer second = new CacheServer("bar", ofMillis(100), 0.5);

		//when
		final Mono<String> response = null;

		//then
		response
				.as(StepVerifier::create)
				.expectNextMatches(s -> s.startsWith("Value-1 from"))
				.verifyComplete();
	}

	/**
	 * TODO Add fallback to {@link Flux#timeout(Duration)}
	 * It should return -1 when timeout of 100ms occurs.
	 */
	@Test
	public void timeout() throws Exception {
		//given
		final Mono<Long> withTimeout = Mono.delay(ofMillis(200));

		//when
		final Mono<Long> withFallback = withTimeout;

		//then
		withFallback
				.as(StepVerifier::create)
				.expectNext(-1L)
				.verifyComplete();
	}

	/**
	 * TODO Add timeout of 80ms to {@link CacheServer#findBy(int)} method.
	 * <p>
	 *     When timeout occurs, {@link Flux#retry()}. However fail if retry takes more than 5 seconds.
	 * </p>
	 */
	@Test
	public void timeoutAndRetries() throws Exception {
		//given
		CacheServer cacheServer = new CacheServer("foo", ofMillis(100), 0);

		//when
		final Mono<String> withTimeouts = cacheServer
				.findBy(1)
				//TODO Operators here
				;

		//then
		withTimeouts.block();
	}

}
