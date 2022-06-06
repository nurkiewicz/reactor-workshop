package com.nurkiewicz.reactor;

import java.util.concurrent.atomic.AtomicInteger;

import com.nurkiewicz.reactor.samples.RestClient;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@Ignore
public class R010_LetsMeetMono {

	/**
	 * Tip: Avoid block() in production code
	 */
	@Test
	public void helloMono() throws Exception {
		//given
		final Mono<String> reactor = Mono.just("Reactor");

		//when
		final String value = reactor.block();

		//then
		assertThat(value).isEqualTo("Reactor");
	}

	@Test
	public void emptyMono() throws Exception {
		//given
		final Mono<String> reactor = Mono.empty();

		//when
		final String value = reactor.block();

		//then
		assertThat(value).isNull();
	}

	@Test
	public void errorMono() throws Exception {
		//given
		final Mono<String> error = Mono.error(new UnsupportedOperationException("Simulated"));

		//when
		try {
			error.block();
			failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
		} catch (UnsupportedOperationException e) {
			//then
			assertThat(e).hasMessage("Simulated");
		}
	}

	@Test
	public void monoIsEager() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();

		//when
		Mono.just(counter.incrementAndGet());

		//then
		assertThat(counter).hasValue(1);
	}

	@Test
	public void monoIsLazy() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger(0);

		//when
		Mono.fromCallable(() -> counter.incrementAndGet());

		//then
		assertThat(counter).hasValue(0);
	}

	@Test
	public void lazyWithoutCaching() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger(0);
		final Mono<Integer> lazy = Mono.fromCallable(() -> counter.incrementAndGet());

		//when
		final Integer first = lazy.block();
		final Integer second = lazy.block();

		//then
		assertThat(first).isEqualTo(1);
		assertThat(second).isEqualTo(2);
	}

	/**
	 * TODO: use {@link Mono#cache()} operator to call {@link AtomicInteger#incrementAndGet()} only once.
	 */
	@Test
	public void cachingMonoComputesOnlyOnce() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger(0);
		final Mono<Integer> lazy = Mono.fromCallable(counter::incrementAndGet);

		//when
		lazy.block();
		lazy.block();

		//then
		assertThat(counter).hasValue(1);
	}

	/**
	 * TODO Use {@link Mono#cache()} to avoid calling destructive method twice
	 */
	@Test
	public void nonIdempotentWebService() throws Exception {
		//given
		RestClient restClient = new RestClient();
		final Mono<Object> result = Mono.fromRunnable(() -> restClient.post(1));

		//when
		result.block();
		result.block();

		//then
		//no exceptions
	}

}
