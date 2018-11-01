package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.samples.Item;
import com.nurkiewicz.reactor.samples.Order;
import com.nurkiewicz.reactor.samples.User;
import com.nurkiewicz.reactor.samples.UserOrders;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.of;
import static java.time.Month.*;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R031_FlatMap {

	private static final Logger log = LoggerFactory.getLogger(R031_FlatMap.class);
	private static final Flux<User> USERS = Flux.just(8, 11, 12).map(User::new);

	@Test
	public void nestedSubscribingIsVeryBad() throws Exception {
		//when
		final Flux<Mono<Order>> orders = USERS.map(user -> UserOrders.lastOrderOf(user));

		//then
		orders.subscribe(
				orderMono -> orderMono.subscribe(
						order -> log.info("Order {}", order.toString())
				)
		);
	}

	/**
	 * TODO Why <code>expectNextMatches</code> only twice?
	 */
	@Test
	public void flatMapToTheRescue() throws Exception {
		//when
		final Flux<Order> orders = USERS.flatMap(user -> UserOrders.lastOrderOf(user));

		//then
		orders
				.as(StepVerifier::create)
				.expectNextMatches(order -> order.getItems().equals(of(new Item("Item of 11"))))
				.expectNextMatches(order -> order.getItems().equals(of(new Item("Item of 12"))))
				.verifyComplete();
	}

	/**
	 * TODO Use {@link Flux#flatMap(Function)} twice to obtain <code>Flux&lt;Item&gt;</code>
	 * <p>
	 * Hint: consider {@link Flux#flatMapIterable(Function)}
	 * </p>
	 */
	@Test
	public void flattenTwice() throws Exception {
		//given

		//when
		final Flux<Item> items = null;  //USERS.flatMap...

		//then
		items
				.as(StepVerifier::create)
				.expectNext(new Item("Item of 11"))
				.expectNext(new Item("Item of 12"))
				.verifyComplete();
	}

	/**
	 * TODO Use {@link Flux#flatMap(Function)} to flatten "broken" stream
	 */
	@Test
	public void flatMapToFlattenExistingStream() throws Exception {
		//given
		//DON'T CHANGE THIS LINE
		final Flux<Mono<Order>> nested = USERS.map(UserOrders::lastOrderOf);

		//when
		Flux<Order> orders = null;  //TODO

		//then
		orders
				.as(StepVerifier::create)
				.expectNextCount(2)
				.verifyComplete();
	}

	@Test
	public void flatMapDoesNotPreserveOrder() throws Exception {
		//given
		final Mono<String> first = Mono.delay(Duration.ofSeconds(2)).map(x -> "first");
		final Mono<String> second = Mono.delay(Duration.ofSeconds(1)).map(x -> "second");

		//when
		final Flux<String> results = Flux
				.just(1, 2)
				.flatMap(x -> x == 1 ? first : second);

		//then
		results
				.as(StepVerifier::create)
				.expectNext("second")
				.expectNext("first")
				.verifyComplete();
	}

	@Test
	public void flatMapTerminatesOnError() throws Exception {
		//given

		//when
		final Flux<LocalDate> dates = Flux
				.just(1, 2, 3)
				.flatMap(x -> failsOnTwo(x));

		//then
		dates
				.as(StepVerifier::create)
				.expectNext(LocalDate.of(2018, JANUARY, 1))
				.expectNext(LocalDate.of(2018, FEBRUARY, 1))
				.expectNext(LocalDate.of(2018, MARCH, 1))
				.expectNextCount(12 - 3) //months left
				.verifyErrorMatches(e -> e.getMessage().equals("Two!"));
	}

	private Flux<LocalDate> failsOnTwo(Integer day) {
		if (day == 2) {
			throw new IllegalArgumentException("Two!");
		}
		return Flux
				.just(Month.values())
				.map(m -> LocalDate.of(2018, m, day));
	}

	@Test
	public void chessboard() throws Exception {
		//given
		final Flux<Integer> rows = Flux.range(1, 8);
		final Flux<String> cols = Flux.just("a", "b", "c", "d", "e", "f", "g", "h");

		//when
		final Flux<String> allFields = rows.flatMap(row -> cols.map(col -> col + row));

		//then
		final List<String> fields = allFields.collectList().block();
		assertThat(fields)
				.hasSize(8 * 8)
				.contains("a1", "a2", "b1", "b2", "h1", "h8");
	}

}