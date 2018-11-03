package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@Ignore
public class R011_LetsMeetFlux {

	@Test
	public void helloFlux() throws Exception {
		//given
		final Flux<String> reactor = Flux.just("Reactor");

		//when
		final List<String> value = reactor.collectList().block();

		//then
		assertThat(value).containsExactly("Reactor");
	}

	@Test
	public void emptyFlux() throws Exception {
		//given
		final Flux<String> reactor = Flux.empty();

		//when
		final List<String> value = reactor.collectList().block();

		//then
		assertThat(value).isEmpty();
	}

	@Test
	public void manyValues() throws Exception {
		//given
		final Flux<String> reactor = Flux.just("Reactor", "library");

		//when
		final List<String> value = reactor.collectList().block();

		//then
		assertThat(value).containsExactly("Reactor", "library");
	}

	@Test
	public void errorFlux() throws Exception {
		//given
		final Flux<String> error = Flux.error(new UnsupportedOperationException("Simulated"));

		//when
		try {
			error.collectList().block();
			failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
		} catch (UnsupportedOperationException e) {
			//then
			assertThat(e).hasMessage("Simulated");
		}
	}

	@Test
	public void concatTwoFluxes() throws Exception {
		//given
		final Flux<String> many = Flux.concat(
				Flux.just("Hello"),
				Flux.just("reactive", "world")
		);

		//when
		final List<String> values = many.collectList().block();

		//then
		assertThat(values).containsExactly("Hello", "reactive", "world");
	}

	@Test
	public void errorFluxAfterValues() throws Exception {
		//given
		final Flux<String> error = Flux.concat(
				Flux.just("Hello", "world"),
				Flux.error(new UnsupportedOperationException("Simulated"))
		);

		//when
		try {
			error.collectList().block();
			failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
		} catch (UnsupportedOperationException e) {
			//then
			assertThat(e).hasMessage("Simulated");
		}
	}

	@Test
	public void fluxIsEager() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();

		//when
		Flux.just(counter.incrementAndGet(), counter.incrementAndGet());

		//then
		assertThat(counter).hasValue(2);
	}

	@Test
	public void fluxIsLazy() throws Exception {
		//given
		AtomicInteger c = new AtomicInteger();

		//when
		Flux.fromStream(() -> Stream.of(c.incrementAndGet(), c.incrementAndGet()));

		//then
		assertThat(c.get()).isZero();
	}

	@Test
	public void fluxComputesManyTimes() throws Exception {
		//given
		AtomicInteger c = new AtomicInteger();
		final Flux<Integer> flux = Flux.fromStream(() ->
				Stream.of(c.incrementAndGet(), c.incrementAndGet()));

		//when
		final List<Integer> first = flux.collectList().block();
		final List<Integer> second = flux.collectList().block();

		//then
		assertThat(c).hasValue(4);
		assertThat(first).containsExactly(1, 2);
		assertThat(second).containsExactly(3, 4);
	}

	/**
	 * TODO Make sure Flux is computed only once
	 * Hint: {@link Flux#cache()}
	 */
	@Test
	public void makeLazyComputeOnlyOnce() throws Exception {
		//given
		AtomicInteger c = new AtomicInteger();
		Flux<Integer> flux = Flux.fromStream(() ->
				Stream.of(c.incrementAndGet(), c.incrementAndGet()))
				.cache();

		//when
		final List<Integer> first = flux.collectList().block();
		final List<Integer> second = flux.collectList().block();

		//then
		assertThat(c).hasValue(2);
		assertThat(first).isEqualTo(second);
	}

}
