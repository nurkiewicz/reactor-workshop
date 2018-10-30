package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.ThreadLocalRandom;

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
	 * Generate a stream of random <code>true</code> and <code>false</code>
	 * @throws Exception
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
//		assertThat(trues + falses).isEqualTo(total);
	}

}
