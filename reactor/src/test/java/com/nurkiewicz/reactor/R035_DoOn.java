package com.nurkiewicz.reactor;

import java.math.BigDecimal;
import java.util.concurrent.atomic.LongAdder;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R035_DoOn {

	private static final Logger log = LoggerFactory.getLogger(R035_DoOn.class);

	@Test
	public void monoCallbacks() throws Exception {
		testCallbacks(Mono.just("Hello!"));
	}

	@Test
	public void emptyMonoCallbacks() throws Exception {
		testCallbacks(Mono.empty());
	}

	@Test
	public void neverMonoCallbacks() throws Exception {
		testCallbacks(Mono.never());
	}

	@Test
	public void errorMonoCallbacks() throws Exception {
		testCallbacks(Mono.error(new RuntimeException("Opps, I did it again")));
	}

	private <T> void testCallbacks(Mono<T> mono) {
		mono
				.doOnSubscribe(s -> log.info("Someone subscribed"))
				.doOnNext(x -> log.info("Got {}", x))
				.doOnSuccess(x -> log.info("Success {}", x))
				.doOnEach(s -> log.info("Got signal {}", s))
				.doOnError(e -> log.warn("Got error", e))
				.doOnRequest(n -> log.info("Subscriber requested {}", n))
				.doOnTerminate(() -> log.info("Terminated, reason unknown"))
				.doOnCancel(() -> log.info("Consumer no longer interested"))
				.log(log.getName())
				.subscribe();
	}

	@Test
	public void fluxCallbacks() throws Exception {
		testCallbacks(Flux.just("Hello", "world!"));
	}

	@Test
	public void emptyFluxCallbacks() throws Exception {
		testCallbacks(Flux.empty());
	}

	@Test
	public void neverFluxCallbacks() throws Exception {
		testCallbacks(Flux.never());
	}

	@Test
	public void errorFluxCallbacks() throws Exception {
		final Flux<String> ok = Flux.just("Trying...");
		final Flux<String> error = Flux.error(new RuntimeException("Opps, I did it again"));
		testCallbacks(Flux.concat(ok, error));
	}

	private <T> void testCallbacks(Flux<T> flux) {
		flux
				.doOnSubscribe(s -> log.info("Someone subscribed"))
				.doOnNext(x -> log.info("Got {}", x))
				.doOnEach(s -> log.info("Got signal {}", s))
				.doOnError(e -> log.warn("Got error", e))
				.doOnRequest(n -> log.info("Subscriber requested {}", n))
				.doOnComplete(() -> log.info("Completed"))
				.doOnTerminate(() -> log.info("Terminated, reason unknown"))
				.subscribe();
	}

	/**
	 * TODO Parse each number using {@link #tryParse(String)}
	 * <p>
	 * Count how many input and output numbers you found and if there was an error
	 * </p>
	 */
	@Test
	public void countEvents() throws Exception {
		//given
		LongAdder inputCount = new LongAdder();
		LongAdder outputCount = new LongAdder();
		LongAdder errorCount = new LongAdder();

		//when
		Flux
				.just("1.00", "2.500", "3", "meh")
				.subscribe(
						x -> log.info("Received {}", x),
						e -> log.warn("Stack-trace intentionally left blank: {}", e.toString())
				);
		//TODO add operators here

		//then
		assertThat(inputCount.sum()).isEqualTo(4);
		assertThat(outputCount.sum()).isEqualTo(5);
		assertThat(errorCount.sum()).isEqualTo(1);
	}

	Flux<BigDecimal> tryParse(String s) {
		return Flux.defer(() -> {
			final BigDecimal std = new BigDecimal(s);
			final BigDecimal stripped = std.stripTrailingZeros();
			if (std.equals(stripped)) {
				return Flux.just(std);
			} else {
				return Flux.just(std, stripped);
			}
		});
	}


}
