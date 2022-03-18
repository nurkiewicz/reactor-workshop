package com.nurkiewicz.reactor;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import com.nurkiewicz.reactor.samples.Ping;
import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

public class R041_Window {

	private static final Logger log = LoggerFactory.getLogger(R041_Window.class);

	@Test
	public void window() throws Exception {
		//given
		final Flux<Integer> nums = Flux.range(1, 10);

		//when
		final Flux<Flux<Integer>> windowsBadly = nums.window(3);
		final Flux<List<Integer>> windows = nums
				.window(3)
				.flatMap(Flux::collectList);

		//then
		windows
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
		final Flux<List<Integer>> windows = nums
				.window(3, 2)
				.flatMap(Flux::collectList);

		//then
		windows
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
		final Flux<List<Integer>> windows = nums
				.window(2, 3)
				.flatMap(Flux::collectList);

		//then
		windows
				.as(StepVerifier::create)
				.expectNext(List.of(1, 2))
				.expectNext(List.of(4, 5))
				.expectNext(List.of(7, 8))
				.expectNext(List.of(10))
				.verifyComplete();
	}

	/**
	 * TODO Find every third word in a sentence using {@link Flux#window(int, int)}
	 * <p>
	 * Hint: {@link Flux#skip(long)} <i>may</i> also help, or maybe {@link Flux#next()} that yields first element?
	 * </p>
	 */
	@Test
	public void everyThirdWord() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words()).take(14);

		//when
		final Flux<String> third = words
				.skip(2)
				.window(3)
				.flatMap(Flux::next);

		//then
		assertThat(third.collectList().block())
				.containsExactly("dolor", "consectetur", "Proin", "suscipit");
	}

	/**
	 * TODO Count how many frames there are approximately per second
	 * <p>
	 * Hint: use {@link Flux#window(Duration)} and most likely {@link Flux#count()}
	 * </p>
	 */
	@Test
	public void countFramesPerSecond() throws Exception {
		//given
		final Flux<Long> frames = Flux.interval(Duration.ofNanos(16667 * 1000));

		//when
		final Flux<Integer> fps = frames
				.window(ofSeconds(1))
				.flatMap(Flux::count)
				.map(Long::intValue)
				.take(4);

		//then
		fps
				.take(4)
				.as(StepVerifier::create)
				.expectNextMatches(x -> x >= 55 && x <= 65)
				.expectNextMatches(x -> x >= 55 && x <= 65)
				.expectNextMatches(x -> x >= 55 && x <= 65)
				.expectNextMatches(x -> x >= 55 && x <= 65)
				.verifyComplete();
	}

	/**
	 * TODO using moving, overlapping window discover three subsequent false values
	 * <p>
	 * Hint: use {@link Flux#window(Duration)} and {@link Flux#doOnNext(Consumer)} to troubleshoot.
	 * Also try {@link Flux#any(Predicate)}
	 * </p>
	 */
	@Test
	public void discoverIfThreeSubsequentPingsFailed() throws Exception {
		//given
		final Flux<Boolean> pings = Ping.checkConstantly("buggy.com");

		//when
		Flux<Boolean> windowPings = pings
				.window(3, 1)
				.flatMap(win -> win.any(x -> x))
				.take(12);

		//then
		windowPings
				.as(StepVerifier::create)
				.expectNext(true)   // true, true, true
				.expectNext(true)   // true, true, false
				.expectNext(true)   // true, false, true
				.expectNext(true)   // false, true, false
				.expectNext(true)   // true, false, false
				.expectNext(true)   // false, false, true
				.expectNext(true)   // false, true, true
				.expectNext(true)   // true, true, false
				.expectNext(true)   // true, false, false
				.expectNext(false)  // false, false, false
				.expectNext(true)   // false, false, true  <-- overflow, starting all over again
				.expectNext(true)   // false, true, true
				.verifyComplete();
	}

}
