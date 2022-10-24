package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R03_MonoSubscribing {

	private static final Logger log = LoggerFactory.getLogger(R03_MonoSubscribing.class);

	@Test
	public void noWorkHappensWithoutSubscription() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();

		//when
		log.info("About to create Mono");
		Mono.fromCallable(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return 42;
		});
		log.info("Mono was created");

		//then
		assertThat(flag).isFalse();
	}

	/**
	 * Notice on which thread everything runs
	 */
	@Test
	public void blockTriggersWork() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();

		//when
		log.info("About to create Mono");
		final Mono<Integer> work = Mono.fromCallable(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return 42;
		});
		log.info("Mono was created");
		final Integer result = work.block();
		log.info("Work is done");

		//then
		assertThat(flag).isTrue();
		assertThat(result).isEqualTo(42);
	}

	@Test
	public void subscriptionTriggersWork() throws Exception {
		//given
		log.info("About to create Mono");

		//when
		final Mono<Integer> work = Mono.fromCallable(() -> {
			log.info("Doing hard work");
			return 42;
		});

		//then
		log.info("Mono was created");

		work.subscribe(i -> log.info("Received {}", i));

		log.info("Work is done");
	}

	@Test
	public void subscriptionOfManyNotifications() throws Exception {
		//given
		log.info("About to create Mono");

		//when
		final Mono<Integer> work = Mono.fromCallable(() -> {
			log.info("Doing hard work");
			return 42;
		});

		//then
		log.info("Mono was created");

		work.subscribe(
				i -> log.info("Received {}", i),
				ex -> log.error("Opps!", ex),
				() -> log.info("Mono completed")
		);

		log.info("Work is done");
	}

	private final AtomicBoolean onNext = new AtomicBoolean();
	private final AtomicReference<Throwable> error = new AtomicReference<>();
	private final AtomicBoolean completed = new AtomicBoolean();

	/**
	 * TODO create a {@link Mono} that completes with an error
	 */
	@Test
	public void monoCompletingWithError() {
		//given

		//when
		final Mono<Integer> work = null;

		//then
		work.subscribe(
				i -> onNext.set(true),
				ex -> error.set(ex),
				() -> completed.set(true)
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext).isFalse();
		assertThat(error.get())
				.isInstanceOf(IOException.class)
				.hasMessage("Simulated");
		assertThat(completed).isFalse();
	}

	/**
	 * TODO create a {@link Mono} that completes normally without emitting any value
	 */
	@Test
	public void monoCompletingWithoutAnyValue() throws Exception {
		//given

		//when
		final Mono<Integer> work = null;

		//then
		work.subscribe(
				i -> onNext.set(true),
				ex -> error.set(ex),
				() -> completed.set(true)
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext).isFalse();
		assertThat(error).hasValue(null);
		assertThat(completed).isTrue();
	}

	/**
	 * TODO create a {@link Mono} that never completes
	 * What happens if you {@link Mono#block()} on such {@link Mono}?
	 * Hint: {@link Mono#never()}
	 */
	@Test
	public void monoThatNeverCompletesAtAll() throws Exception {
		//given

		//when
		final Mono<Integer> work = null;

		//then
		work.subscribe(
				i -> onNext.set(true),
				ex -> error.set(ex),
				() -> completed.set(true)
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext).isFalse();
		assertThat(error).hasValue(null);
		assertThat(completed).isFalse();
	}

}
