package com.nurkiewicz.reactor;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.nurkiewicz.reactor.samples.CacheServer;
import com.nurkiewicz.reactor.user.User;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R045_ErrorHandling {

	private static final Logger log = LoggerFactory.getLogger(R045_ErrorHandling.class);

	@Test
	public void onErrorReturn() throws Exception {
		//given
		final Mono<String> err = Mono.error(new RuntimeException("Opps"));

		//when
		final Mono<String> withFallback = err.onErrorReturn("Fallback");

		//then
		err
				.as(StepVerifier::create)
				.verifyErrorMessage("Opps");
		withFallback
				.as(StepVerifier::create)
				.expectNext("Fallback")
				.verifyComplete();
	}

	/**
	 * TODO Where's the 'Failed' exception? Add some logging
	 */
	@Test
	public void onErrorResume() throws Exception {
		//given
		AtomicBoolean cheapFlag = new AtomicBoolean();
		AtomicBoolean expensiveFlag = new AtomicBoolean();

		Mono<String> cheapButDangerous = Mono.fromCallable(() -> {
			cheapFlag.set(true);
			throw new RuntimeException("Failed");
		});

		Mono<String> expensive = Mono.fromCallable(() -> {
			expensiveFlag.set(true);
			return "Expensive";
		});

		//when
		final Mono<String> withError = cheapButDangerous
				.onErrorResume(e -> expensive);

		//then
		withError
				.as(StepVerifier::create)
				.expectNext("Expensive")
				.verifyComplete();
		assertThat(cheapFlag).isTrue();
		assertThat(expensiveFlag).isTrue();
	}

	/**
	 * TODO Return different value for {@link IllegalStateException} and different for {@link IllegalArgumentException}
	 *
	 * @throws Exception
	 */
	@Test
	public void handleExceptionsDifferently() throws Exception {
		handle(danger(1))
				.as(StepVerifier::create)
				.expectNext(-1)
				.verifyComplete();

		handle(danger(2))
				.as(StepVerifier::create)
				.expectNext(-2)
				.verifyComplete();

		handle(danger(3))
				.as(StepVerifier::create)
				.verifyErrorMessage("Other: 3");
	}

	/**
	 * TODO Add error handling
	 *
	 * @see Mono#onErrorResume(Function)
	 */
	private Mono<Integer> handle(Mono<Integer> careful) {
		return careful;
	}

	private Mono<Integer> danger(int id) {
		return Mono.fromCallable(() -> {
			switch (id) {
				case 1:
					throw new IllegalArgumentException("One");
				case 2:
					throw new IllegalStateException("Two");
				default:
					throw new RuntimeException("Other: " + id);
			}
		});
	}

	@Test
	public void simpleRetry() throws Exception {
		//given
		CacheServer cacheServer = new CacheServer("foo.com", Duration.ofMillis(500), 1);

		//when
		final Mono<String> retried = cacheServer
				.findBy(1)
				.retry(4);

		//then
		retried
				.as(StepVerifier::create)
				.verifyErrorMessage("Simulated fault");
	}

	/**
	 * TODO Why this test never finishes? Add some logging and fix {@link #broken()} method.
	 */
	@Test
	public void fixEagerMono() throws Exception {
		//given
		final Mono<User> mono = broken();

		//when
		final Mono<User> retried = mono.retry(1_000);

		//then
		retried
				.as(StepVerifier::create)
				.expectNext(new User(1))
				.verifyComplete();
	}

	Mono<User> broken() {
		double rand = ThreadLocalRandom.current().nextDouble();
		if (rand > 0.1) {
			return Mono
					.<User>error(new RuntimeException("Too big value " + rand))
					.delaySubscription(Duration.ofMillis(10));
		}
		return Mono.just(new User(1));
	}

	@Test
	public void retryWithExponentialBackoff() throws Exception {
		final RetryBackoffSpec spec = Retry
				.backoff(20, Duration.ofMillis(100))
				.jitter(0.2)
				.maxBackoff(Duration.ofSeconds(2));
		Mono
				.error(new RuntimeException("Opps"))
				.doOnError(x -> log.warn("Exception: {}", x.toString()))
				.retryWhen(spec)
				.subscribe();
		TimeUnit.SECONDS.sleep(5);
	}

}
