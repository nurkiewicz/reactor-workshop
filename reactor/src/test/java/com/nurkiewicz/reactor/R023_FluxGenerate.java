package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R023_FluxGenerate {

	private static final Logger log = LoggerFactory.getLogger(R023_FluxGenerate.class);

	/**
	 * Backpressure-aware
	 */
	@Test
	public void generateRandomStreamOfNumbers() throws Exception {
		//given
		final Flux<Float> randoms = Flux
				.generate(sink ->
				{
					final float rand = ThreadLocalRandom.current().nextFloat();
					log.debug("Returning {}", rand);
					sink.next(rand);
				});

		//when
		final Flux<Float> twoRandoms = randoms
				.take(2);

		//then
		twoRandoms
				.as(StepVerifier::create)
				.expectNextMatches(x -> x >= 0 && x < 1)
				.expectNextMatches(x -> x >= 0 && x < 1)
				.verifyComplete();
	}

	/**
	 * TODO Generate a stream of random <code>true</code> and <code>false</code>
	 */
	@Test
	public void generateRandomBooleans() throws Exception {
		//given
		final int total = 1_000;
		final Flux<Boolean> randoms = Flux
				.generate(sink -> sink.next(true));

		//when
		final Flux<Boolean> thousandBooleans = randoms.take(total);

		//then
		final Long trues = thousandBooleans.filter(x -> x == true).count().block();
		final Long falses = thousandBooleans.filter(x -> x == false).count().block();
		assertThat(trues).isBetween(400L, 600L);
		assertThat(falses).isBetween(400L, 600L);

		//and
		//TODO Uncomment line below. Why does it fail?
		//Fix it
//		assertThat(trues + falses).isEqualTo(total);
	}

	@Test
	public void statefulGenerate() throws Exception {
		//given
		final Flux<Integer> naturals = Flux.generate(() -> 0, (state, sink) -> {
			sink.next(state);
			return state + 1;
		});

		//when
		final Flux<Integer> three = naturals
				.take(3);

		//then
		three
				.as(StepVerifier::create)
				.expectNext(0)
				.expectNext(1)
				.expectNext(2)
				.verifyComplete();
	}

	/**
	 * TODO Implement stream of powers of two (1, 2, 4, 8, 16)
	 */
	@Test
	public void powersOfTwo() throws Exception {
		//given
		final Flux<BigInteger> naturals = Flux.generate(() -> ONE, (state, sink) -> {
			//TODO
			return state;
		});

		//when
		final Flux<BigInteger> three = naturals
				.skip(10)
				.take(3);

		//then
		three
				.as(StepVerifier::create)
				.expectNext(BigInteger.valueOf(1024))
				.expectNext(BigInteger.valueOf(2048))
				.expectNext(BigInteger.valueOf(4096))
				.verifyComplete();
	}

	@Test
	public void readingFileOneByOne() throws Exception {
		//given

		//when

		//then
	}

}
