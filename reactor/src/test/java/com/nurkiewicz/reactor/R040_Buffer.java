package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R040_Buffer {

	private static final Logger log = LoggerFactory.getLogger(R040_Buffer.class);

	@Test
	public void buffer() throws Exception {
		//given
		final Flux<Integer> nums = Flux.range(1, 10);

		//when
		final Flux<List<Integer>> buffers = nums.buffer(3);

		//then
		buffers
				.as(StepVerifier::create)
				.expectNext(List.of(1, 2, 3))
				.expectNext(List.of(4, 5, 6))
				.expectNext(List.of(7, 8, 9))
				.expectNext(List.of(10))
				.verifyComplete();
	}

	@Test
	public void overlapping() throws Exception {
		//given
		final Flux<Integer> nums = Flux.range(1, 8);

		//when
		final Flux<List<Integer>> buffers = nums.buffer(3, 2);

		//then
		buffers
				.as(StepVerifier::create)
				.expectNext(List.of(1, 2, 3))
				.expectNext(List.of(3, 4, 5))
				.expectNext(List.of(5, 6, 7))
				.expectNext(List.of(7, 8))
				.verifyComplete();
	}

	@Test
	public void gaps() throws Exception {
		//given
		final Flux<Integer> nums = Flux.range(1, 10);

		//when
		final Flux<List<Integer>> buffers = nums.buffer(2, 3);

		//then
		buffers
				.as(StepVerifier::create)
				.expectNext(List.of(1, 2))
				.expectNext(List.of(4, 5))
				.expectNext(List.of(7, 8))
				.expectNext(List.of(10))
				.verifyComplete();
	}

	/**
	 * TODO Find every third word in a sentence using {@link Flux#buffer(int, int)}
	 * <p>
	 * Hint: {@link Flux#skip(long)} <i>may</i> also help
	 * </p>
	 */
	@Test
	public void everyThirdWord() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words()).take(14);

		//when
		final Flux<String> third = words;

		//then
		assertThat(third.collectList().block())
				.containsExactly("dolor", "consectetur", "Proin", "suscipit");
	}


}
