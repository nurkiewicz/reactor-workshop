package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.samples.Weather;
import com.nurkiewicz.reactor.samples.WeatherService;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.BiPredicate;
import java.util.function.Function;

@Ignore
public class R034_Distinct {

	private static final Logger log = LoggerFactory.getLogger(R034_Distinct.class);

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
	 * greater than 0.5
	 * @throws Exception
	 */
	@Test
	public void reportWeatherOnlyWhenItChangesEnough() throws Exception {
		//given
		final Flux<Weather> measurements = WeatherService.measurements();

		//when
		final Flux<Weather> changes = measurements.distinctUntilChanged(
				Weather::getTemperature,
				(x, y) -> Math.abs(x - y) < 0.5);

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

}
