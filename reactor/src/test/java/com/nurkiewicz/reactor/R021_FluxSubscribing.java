package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R021_FluxSubscribing {

	private static final Logger log = LoggerFactory.getLogger(R021_FluxSubscribing.class);

	@Test
	public void noWorkHappensWithoutSubscription() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();

		//when
		log.info("About to create Flux");
		Flux.fromStream(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return Stream.of(1, 2, 3);
		});

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
		log.info("About to create Flux");
		final Flux<Integer> work = Flux.fromStream(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return Stream.of(1, 2, 3);
		});
		log.info("Flux was created");
		final Integer result = work.blockLast();
		log.info("Work is done");

		//then
		assertThat(flag).isTrue();
		assertThat(result).isEqualTo(3);
	}

	@Test
	public void subscriptionTriggersWork() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();
		log.info("About to create Flux");

		//when
		final Flux<Integer> work = Flux.fromStream(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return Stream.of(1, 2, 3);
		});

		//then
		log.info("Flux was created");

		work.subscribe(i -> log.info("Received {}", i));

		log.info("Work is done");
	}

	@Test
	public void subscriptionOfManyNotifications() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();
		log.info("About to create Flux");

		//when
		final Flux<Integer> work = Flux.fromStream(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return Stream.of(1, 2, 3);
		});

		//then
		log.info("Flux was created");

		work.subscribe(
				i -> log.info("Received {}", i),
				ex -> log.error("Opps!", ex),
				() -> log.info("Flux completed")
		);

		log.info("Work is done");
	}

	private final List<Integer> onNext = new CopyOnWriteArrayList<>();
	private final AtomicReference<Throwable> error = new AtomicReference<>();
	private final AtomicBoolean completed = new AtomicBoolean();

	/**
	 * TODO create a {@link Flux} that completes with an error immediately
	 */
	@Test
	public void fluxCompletingWithError() {
		//given

		//when
		final Flux<Integer> work = null;

		//then
		work.subscribe(
				onNext::add,
				error::set,
				() -> completed.set(true)
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext).isEmpty();
		assertThat(error.get())
				.isInstanceOf(IOException.class)
				.hasMessage("Simulated");
		assertThat(completed).isFalse();
	}

	/**
	 * TODO create a {@link Flux} that completes normally without emitting any value
	 */
	@Test
	public void fluxCompletingWithoutAnyValue() throws Exception {
		//given

		//when
		final Flux<Integer> work = null;

		//then
		work.subscribe(
				onNext::add,
				error::set,
				() -> completed.set(true)
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext).isEmpty();
		assertThat(error).hasValue(null);
		assertThat(completed).isTrue();
	}

	/**
	 * TODO create a {@link Flux} that <b>fails</b> after emitting few values
	 * Hint: {@link Flux#concat(Publisher[])} with failing {@link Flux} as second argument
	 */
	@Test
	public void fluxThatFailsAfterEmitting() throws Exception {
		//given

		//when
		final Flux<Integer> work = Flux.concat(
				null,
				null
		);

		//then
		work.subscribe(
				onNext::add,
				error::set,
				() -> completed.set(true)
		);

		assertThat(onNext).containsExactly(1, 2, 3);
		assertThat(error.get())
				.isInstanceOf(IOException.class)
				.hasMessage("Simulated");
		assertThat(completed).isFalse();
	}

	/**
	 * TODO create a {@link Flux} that never completes <b>after</b> emitting few values.
	 * What happens if you {@link Flux#blockLast()} on such {@link Flux}?
	 * Hint: {@link Flux#concat(Publisher[])}
	 */
	@Test
	public void fluxThatNeverCompletesAtAll() throws Exception {
		//given

		//when
		final Flux<Integer> work = null;

		//then
		work.subscribe(
				onNext::add,
				error::set,
				() -> completed.set(true)
		);

		assertThat(onNext).containsExactly(1, 2, 3);
		assertThat(error).hasValue(null);
		assertThat(completed).isFalse();
	}

}
