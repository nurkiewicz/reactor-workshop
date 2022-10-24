package com.nurkiewicz.reactor;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import com.nurkiewicz.reactor.samples.Ping;
import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class R15_Buffer {

	private static final Logger log = LoggerFactory.getLogger(R15_Buffer.class);

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
		final Flux<String> third = words
				.skip(2)
				.buffer(3)
				.map(list -> list.get(0));

		//then
		assertThat(third.collectList().block())
				.containsExactly("dolor", "consectetur", "Proin", "suscipit");
	}

	@Test
	public void interval() throws Exception {
		final Flux<Long> frames = Flux.interval(Duration.ofMillis(16));

		frames
				.take(120)
				.subscribe(
						x -> log.info("Got frame {}", x)
				);

		TimeUnit.SECONDS.sleep(3);  //Why ???
	}

	/**
	 * TODO Count how many frames there are approximately per second
	 * <p>
	 * Hint: use {@link Flux#buffer(Duration)} and most likely {@link Flux#map(Function)}
	 * </p>
	 */
	@Test
	public void countFramesPerSecond() throws Exception {
		//given
		final Flux<Long> frames = Flux.interval(Duration.ofMillis(16));

		//when
		//TODO operator here, add take(4)
		final Flux<Integer> fps = frames
				.buffer(Duration.ofSeconds(1))
				.map(List::size)
				.take(4);;

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
	 * TODO Ping host every 500ms using {@link Flux#interval(Duration)}
	 * <p>
	 * Show result using {@link Mono#doOnSuccess(Consumer)}
	 * </P>
	 */
	@Test
	public void pingHost() throws Exception {
		//given

		//then
		Flux
				.interval(Duration.ofMillis(500))
				.flatMap(x -> Ping.check("example.com").doOnSuccess(v -> log.debug("Pong")))
				.subscribe(
						v -> log.info("Will never happen anyway")
				);

		TimeUnit.SECONDS.sleep(10);
	}

	/**
	 * TODO If Mono is empty, turn it into true. If it terminates with an error, make it false
	 *
	 * @see Mono#switchIfEmpty(Mono)
	 * @see Mono#onErrorReturn(Object)
	 */
	@Test
	public void turnVoidToTrueFalse() throws Exception {
		assertThat(toTrueFalse(Mono.empty()).block()).isEqualTo(true);
		assertThat(toTrueFalse(Mono.error(new RuntimeException())).block()).isEqualTo(false);
	}

	/**
	 * TODO Implement this
	 */
	static Mono<Boolean> toTrueFalse(Mono<Boolean> v) {
		return v
				.switchIfEmpty(Mono.just(true))
				.onErrorReturn(false);
	}

	/**
	 * TODO using moving, overlapping window discover three subsequent false values
	 * <p>
	 * Hint: use {@link Flux#buffer(int, int)} and {@link Flux#doOnNext(Consumer)} to troubleshoot
	 * </p>
	 */
	@Test
	public void discoverIfThreeSubsequentPingsFailed() throws Exception {
		//given
		final Flux<Boolean> pings = Ping.checkConstantly("buggy.com");

		//when
		Flux<Boolean> bufferPings = pings
				.buffer(3, 1)
				.doOnNext(System.out::println)
				.map(list -> list.stream().anyMatch(x -> x))
				.take(12);

		//then
		bufferPings
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
