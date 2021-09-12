package com.nurkiewicz.reactor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.nurkiewicz.reactor.samples.Ping;
import com.nurkiewicz.reactor.user.Order;
import com.nurkiewicz.reactor.user.User;
import com.nurkiewicz.reactor.user.UserOrders;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import static org.assertj.core.api.Assertions.assertThat;

public class R043_Zip {

	private static final Logger log = LoggerFactory.getLogger(R043_Zip.class);

	@Test
	public void zipTwoStreams() throws Exception {
		//given
		final Flux<Integer> nums = Flux.just(1, 2, 3);
		final Flux<String> strs = Flux.just("a", "b");

		//when
		final Flux<Tuple2<Integer, String>> pairs = nums.zipWith(strs);
		final Flux<Tuple2<Integer, String>> pairs2 = Flux.zip(nums, strs);  //same thing

		//then
		pairs.subscribe(p -> log.info("Pair: {}", p));
	}

	@Test
	public void customCombinator() throws Exception {
		//given
		final Flux<Integer> nums = Flux.just(1, 2, 3);
		final Flux<String> strs = Flux.just("a", "bc", "def");

		//when
		final Flux<Double> doubles = Flux.zip(
				nums,
				strs,
				(n, s) -> n * s.length() * 2.0
		);

		//then
		doubles
				.as(StepVerifier::create)
				.expectNext(2.0, 8.0, 18.0)
				.verifyComplete();
	}

	@Test
	public void pairwise() throws Exception {
		//given
		final Flux<Long> fast = Flux.interval(Duration.ofMillis(200));
		final Flux<Long> slow = Flux.interval(Duration.ofMillis(250));

		//when
		Flux.zip(
				fast, slow
		).subscribe(
				pair -> log.info("Got {}", pair)
		);

		//then
		TimeUnit.SECONDS.sleep(3);
	}

	/**
	 * TODO Increase sleep at the end. You should see an exception after a while
	 */
	@Test
	public void latest() throws Exception {
		//given
		final Flux<Long> fast = Flux.interval(Duration.ofMillis(100)).delayElements(Duration.ofMillis(1000));
		final Flux<Long> slow = Flux.interval(Duration.ofMillis(250));

		//when
		Flux.combineLatest(
				fast, slow,
				Tuples::of
		).subscribe(
				pair -> log.info("Got {}", pair)
		);

		//then
		TimeUnit.SECONDS.sleep(3);
	}

	@Test
	public void errorBreaksZip() throws Exception {
		//given
		final Flux<Integer> nums = Flux.just(1, 2, 3);
		final Flux<String> strs = Flux.concat(
				Flux.just("a", "b"),
				Flux.error(new RuntimeException("Opps"))
		);

		//when
		final Flux<Tuple2<Integer, String>> pairs = nums.zipWith(strs);

		//then
		pairs
				.as(StepVerifier::create)
				.expectNext(Tuples.of(1, "a"))
				.expectNext(Tuples.of(2, "b"))
				.verifyErrorMatches(e -> e.getMessage().equals("Opps"));
	}

	@Test
	public void monoCompleted() throws Exception {
		//given
		final Mono<Integer> num = Mono.just(1);
		final Mono<String> str = Mono.just("a");

		//when
		final Mono<Tuple2<Integer, String>> pair = num.zipWith(str);

		//then
		assertThat(pair.block()).isNotNull();
	}

	@Test
	public void monoOneEmpty() throws Exception {
		//given
		final Mono<Integer> num = Mono.just(1);
		final Mono<String> str = Mono.empty();

		//when
		final Mono<Tuple2<Integer, String>> pair = num.zipWith(str);

		//then
		assertThat(pair.block()).isNull();
	}

	@Test
	public void realLifeMono() throws Exception {
		//given
		final Mono<Order> order = UserOrders.lastOrderOf(new User(20));
		final Mono<Boolean> ping = Ping.checkConstantly("example.com").next();

		//when
		final Mono<Tuple2<Order, Boolean>> result = Mono.zip(
				order,
				ping
		);
	}

}
