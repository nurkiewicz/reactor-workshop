package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R022_HowToCreateMonoAndFlux {

	private static final Logger log = LoggerFactory.getLogger(R022_HowToCreateMonoAndFlux.class);

	@Test
	public void eagerlyEvaluated() throws Exception {
		//when
		Mono.just(destroyEarth());

		//then
		assertThat(destroyed).isTrue();
	}

	@Test
	public void lazilyEvaluateMono() throws Exception {
		//when
		Mono.fromCallable(this::destroyEarth);

		//then
		assertThat(destroyed).isFalse();
	}

	private AtomicBoolean killed = new AtomicBoolean();
	private AtomicBoolean destroyed = new AtomicBoolean();

	@Test
	public void creatingEagerFluxFromStreamIncorrectly() throws Exception {
		//given
		List<Boolean> tasks = Arrays.asList(killHumanity(), destroyEarth());

		//when
		Flux.fromStream(tasks.stream());

		//then
		assertThat(killed).isTrue();
		assertThat(destroyed).isTrue();
	}

	@Test
	public void creatingEagerFluxFromStreamIncorrectly2() throws Exception {
		//when
		Flux.fromStream(Stream.of(killHumanity(), destroyEarth()));

		//then
		assertThat(killed).isTrue();
		assertThat(destroyed).isTrue();
	}

	@Test
	public void creatingLazyFluxFromStreamCorrectly() throws Exception {
		//when
		Flux.fromStream(() -> Stream.of(killHumanity(), destroyEarth()));

		//then
		assertThat(killed).isFalse();
		assertThat(destroyed).isFalse();
	}

	/**
	 * TODO Make sure operations are run only once, despite two subscriptions
	 * @throws Exception
	 */
	@Test
	public void createLazyFluxStreamThatDestroysEarthOnlyOnce() throws Exception {
		//given
		final Flux<Boolean> operations = Flux.fromStream(() -> Stream.of(killHumanity(), destroyEarth()));

		//when
		operations.subscribe();
		operations.subscribe();

		//then
		//no exception thrown
	}

	private boolean killHumanity() {
		log.info("Killed");
		if (!killed.compareAndSet(false, true)) {
			throw new IllegalStateException("Already killed");
		}
		return true;
	}

	private boolean destroyEarth() {
		log.info("Destroyed");
		if (!destroyed.compareAndSet(false, true)) {
			throw new IllegalStateException("Already destroyed");
		}
		return true;
	}

}
