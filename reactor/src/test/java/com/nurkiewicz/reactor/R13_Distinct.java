package com.nurkiewicz.reactor;

import com.devskiller.jfairy.Fairy;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nurkiewicz.reactor.email.Email;
import com.nurkiewicz.reactor.samples.CacheCollectionAdapter;
import com.nurkiewicz.reactor.samples.Weather;
import com.nurkiewicz.reactor.samples.WeatherService;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R13_Distinct {

	private static final Logger log = LoggerFactory.getLogger(R13_Distinct.class);

	private static final Flux<String> words = Flux.just("elit", "elit", "est", "est", "eget", "et", "eget", "erat");

	@Test
	public void distinctWords() throws Exception {
		//when
		final Flux<String> distinct = words.distinct();

		//then
		distinct
				.as(StepVerifier::create)
				.expectNext("elit", "est", "eget", "et", "erat")
				.verifyComplete();
	}

	@Test
	public void distinctLength() throws Exception {
		//given

		//when
		final Flux<String> distinct = words.distinct(String::length);

		//then
		distinct
				.as(StepVerifier::create)
				.expectNext("elit", "est", "et")
				.verifyComplete();
	}

	@Test
	public void distinctWordSequences() throws Exception {
		//when
		final Flux<String> distinct = words.distinctUntilChanged();

		//then
		distinct
				.as(StepVerifier::create)
				.expectNext("elit", "est", "eget", "et", "eget", "erat")
				.verifyComplete();
	}

	/**
	 * TODO Use {@link Flux#distinctUntilChanged(Function, BiPredicate)} to discover temperature changes
	 * greater than or equal 0.5
	 *
	 * @throws Exception
	 */
	@Test
	public void reportWeatherOnlyWhenItChangesEnough() throws Exception {
		//given
		final Flux<Weather> measurements = WeatherService.measurements();

		//when
		final Flux<Weather> changes = measurements;

		//then
		changes
				.map(Weather::getTemperature)
				.as(StepVerifier::create)
				.expectNext(14.0)
				.expectNext(14.5)
				.expectNext(16.0)
				.expectNext(15.2)
				.expectNext(14.0)
				.verifyComplete();
	}

	/**
	 * TODO Create stream of {@link Email} messages using @{link {@link Email#random(Fairy)}} and {@link Flux#generate(Consumer)}
	 */
	@Test
	public void inboxAsStream() throws Exception {
		//given
		final Flux<Email> emails = emails();

		//when
		final Flux<Email> ten = emails.take(10);

		//then
		assertThat(ten.collectList().block()).hasSize(10);

		//alternatively
		ten
				.as(StepVerifier::create)
				.expectNextCount(10)
				.verifyComplete();
	}

	Flux<Email> emails() {
		return emails(Fairy.create());
	}

	/**
	 * TODO Generate infinite stream of e-mails. Use {@link Email#random(Fairy)}
	 */
	Flux<Email> emails(Fairy fairy) {
		return Flux.empty();
	}

	/**
	 * TODO Find first 10 distinct e-mail sends from random sample
	 * <p>
	 * Hint: use parameterless {@link Flux#distinct()}
	 * </p>
	 */
	@Test
	public void findOnlyDistinctEmailSender() throws Exception {
		//given
		final Flux<Email> emails = emails();
		final int total = 10;

		//when
		final Flux<String> distinctSenders = null;

		//then
		final HashSet<String> unique = new HashSet<>(distinctSenders.collectList().block());
		assertThat(unique).hasSize(total);
	}

	/**
	 * TODO Make sure you do not run out of memory by using expiring cache inside {@link Flux#distinct()}
	 * <p>
	 * Observe the output, cache behaves non-deterministically
	 * </p>
	 * Hint: use {@link Flux#distinct(Function, Supplier)} with custom collection
	 */
	@Test
	public void distinctWithin() throws Exception {
		//given
		final Flux<Integer> numbers = Flux.just(1, 2, 3)
				.repeat(1)
				.delayElements(ofMillis(1_000));
		final Collection<Integer> cache = new CacheCollectionAdapter<>(cache(2));

		//when
		final Flux<Integer> distinct = numbers;

		//then
		distinct
				.doOnNext(x -> log.info("Got {}", x))
				.blockLast();
	}

	<T> Cache<T, Boolean> cache(int maxSize) {
		return Caffeine.newBuilder()
				.maximumSize(maxSize)
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.removalListener((key, value, cause) -> log.debug("REMOVE {} ({})", key, cause))
				.build();
	}
}
