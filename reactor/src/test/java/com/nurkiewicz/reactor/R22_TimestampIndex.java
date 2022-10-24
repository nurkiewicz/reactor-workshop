package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static reactor.util.function.Tuples.of;

@Ignore
public class R22_TimestampIndex {

	private static final Logger log = LoggerFactory.getLogger(R22_TimestampIndex.class);

	@Test
	public void index() throws Exception {
		//given
		final Flux<String> strings = Flux.just("A", "B", "C");

		//when
		final Flux<Tuple2<Long, String>> indexed = strings.index();

		//then
		indexed
				.as(StepVerifier::create)
				.expectNext(of(0L, "A"))
				.expectNext(of(1L, "B"))
				.expectNext(of(2L, "C"))
				.verifyComplete();
	}

	/**
	 * TODO Find every third word using {@link Flux#index()}
	 * @throws Exception
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

	@Test
	public void timestamp() throws Exception {
		//given
		final Flux<String> ticker = Flux
				.interval(ofMillis(123))
				.map(lng -> "Item-" + lng);

		//when
		final Flux<Tuple2<Long, String>> stamped = ticker.timestamp();

		//then
		final Flux<Tuple2<Instant, String>> instants = stamped
				.map(tup -> tup.mapT1(Instant::ofEpochMilli));

		instants
				.subscribe(
						x -> log.info("Received {}", x)
				);

		TimeUnit.SECONDS.sleep(4);
	}

	/**
	 * TODO Compute time between events using {@link Flux#timestamp()} and {@link Flux#buffer(int)} or {@link Flux#window(int)}
	 */
	@Test
	public void timeDifference() throws Exception {
		//given
		final Flux<String> ticker = Flux
				.interval(ofMillis(200))
				.map(lng -> "Item-" + lng);

		//when
		final Flux<Long> elapsed = null; // TODO tickers...

		//then
		elapsed
				.take(5)
				.as(StepVerifier::create)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.verifyComplete();
	}

	@Test
	public void elapsed() throws Exception {
		//given
		final Flux<String> ticker = Flux
				.interval(ofMillis(200))
				.map(lng -> "Item-" + lng);

		//when
		final Flux<Tuple2<Long, String>> elapsedPairs = ticker
				.elapsed();

		final Flux<Long> elapsed = elapsedPairs
				.map(Tuple2::getT1)
				.take(5);

		//then
		elapsed
				.as(StepVerifier::create)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.expectNextMatches(x -> x >= 150 && x <= 250)
				.verifyComplete();
	}

	/**
	 * TODO Which pings were slower?
	 * <p>
	 * Pick sequence number of pings that returned > 100ms after request.
	 * Hint: use {@link Flux#index()} and {@link Flux#elapsed()}
	 * </p>
	 */
	@Test
	public void whichEventWasSlow() throws Exception {
		//given

		//when
		final Flux<Long> slowIndices = null;  // Ping.checkConstantly("vary.com")...take(5);

		//then
		slowIndices
				.as(StepVerifier::create)
				.expectNext(3L, 11L, 19L, 27L, 35L)
				.verifyComplete();
	}

}
