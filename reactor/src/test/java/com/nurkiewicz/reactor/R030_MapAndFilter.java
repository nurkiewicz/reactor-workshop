package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R030_MapAndFilter {

	private static final Logger log = LoggerFactory.getLogger(R030_MapAndFilter.class);

	@Test
	public void mapTransformsItemsOnTheFly() throws Exception {
		//given
		final Flux<Integer> numbers = Flux.range(5, 4);

		//when
		final Flux<Integer> even = numbers.map(x -> x * 2);

		//then
		even
				.as(StepVerifier::create)
				.expectNext(10, 12, 14, 16)
				.verifyComplete();
	}

	@Test
	public void mapCanChangeType() throws Exception {
		//given
		final Flux<String> numbers = Flux.just("Lorem", "ipsum", "dolor", "sit", "amet");

		//when
		final Flux<Integer> lengths = numbers.map(String::length);

		//then
		lengths
				.as(StepVerifier::create)
				.expectNext("Lorem".length())
				.expectNext("ipsum".length())
				.expectNext("dolor".length())
				.expectNext("sit".length())
				.expectNext("amet".length())
				.verifyComplete();
	}

	/**
	 * TODO Use {@link Flux#filter(Predicate)}
	 */
	@Test
	public void filterSelectsOnlyMatchingElements() throws Exception {
		//given
		final Flux<String> numbers = Flux.just("Excepteur", "sint", "occaecat", "cupidatat", "non", "proident");

		//when
		final Flux<String> endingWithT = numbers;

		//then
		assertThat(endingWithT.collectList().block()).containsExactly("sint", "occaecat", "cupidatat", "proident");
	}

	/**
	 * TODO only pick words starting with 'e' and ending with 't'. But first remove words ending with comma or dot.
	 */
	@Test
	public void lengthOfAllWordsEndingWithT() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words());
		System.out.println(LoremIpsum.text());

		//when
		final Flux<String> lengths = words;

		System.out.println(lengths.collectList().block());
		//then
		lengths
				.as(StepVerifier::create)
				.expectNext("elit", "elit", "est", "est", "eget", "et", "eget", "erat")
				.verifyComplete();
	}

}
