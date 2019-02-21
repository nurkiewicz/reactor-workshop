package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R042_Casting {

	private static final Logger log = LoggerFactory.getLogger(R042_Casting.class);

	@Test
	public void castingElements() throws Exception {
		//given
		Flux<Number> nums = Flux.just(1, 2, 3);

		//when
		final Flux<Integer> ints = nums.cast(Integer.class);

		//then
		final List<Integer> list = ints.collectList().block();
		assertThat(list).containsExactly(1, 2, 3);
	}

	@Test
	public void castingWithFiltering() throws Exception {
		//given
		Flux<Number> nums = Flux.just(1, 2.0, 3.0f);

		//when
		final Flux<Double> doubles = nums.ofType(Double.class);

		//then
		doubles
				.as(StepVerifier::create)
				.expectNext(2.0)
				.verifyComplete();
	}


}
