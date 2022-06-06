package com.nurkiewicz.reactor;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.nurkiewicz.reactor.user.Item;
import com.nurkiewicz.reactor.user.LoremIpsum;
import com.nurkiewicz.reactor.user.Order;
import com.nurkiewicz.reactor.user.User;
import com.nurkiewicz.reactor.user.UserOrders;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Ignore
public class R032_AdvancedFiltering {

	private static final Logger log = LoggerFactory.getLogger(R032_AdvancedFiltering.class);

	@Test
	public void handleIsBothMapAndFilter() throws Exception {
		//given
		Flux<Order> orders = Flux
				.just(8, 11, 12)
				.map(User::new)
				.flatMap(UserOrders::lastOrderOf);

		//when
		final Flux<ImmutableList<Item>> items = orders
				.handle((order, sink) -> {
					if (!order.getItems().isEmpty()) {
						sink.next(order.getItems());
					}
				});

		//then
		items
				.as(StepVerifier::create)
				.expectNextCount(2)
				.verifyComplete();
	}

	/**
	 * TODO Flatten <code>Flux</code> of lists into <code>Flux</code> of <code>Orders</code>.
	 * Hint: Then use {@link Flux#handle(BiConsumer)} and then {@link Flux#concatMapIterable(Function)}
	 */
	@Test
	public void flattenNestedList() throws Exception {
		//given
		Flux<Order> orders = Flux
				.just(8, 11, 12)
				.map(User::new)
				.flatMap(UserOrders::lastOrderOf);

		//when
		final Flux<Item> items = null; //Hint: start by copy-pasting solution from above using handle()

		//then
		items
				.as(StepVerifier::create)
				.expectNextCount(4)
				.verifyComplete();
	}

	@Test
	public void operatorsAreSingleThreaded() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words());

		//when
		final Flux<String> filtered = words.filter(s -> sha256(s).toString().startsWith("0"));

		//then
		filtered
				.as(StepVerifier::create)
				.expectNext("ipsum")
				.expectNextCount(9)
				.verifyComplete();
	}

	private HashCode sha256(String input) {
		return Hashing
				.sha256()
				.hashString(input, StandardCharsets.UTF_8);
	}

	@Test
	public void brokenFilteringWithBlocking() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words());

		//when
		final Flux<String> filtered = words
				.filter(s -> asyncSha256(s).block().toString().startsWith("0"));  //No, no, NO!

		//then
		filtered
				.as(StepVerifier::create)
				.expectNext("ipsum")
				.expectNextCount(9)
				.verifyComplete();
	}

	/**
	 * TODO Use {@link #asyncSha256(String)} to filter items
	 * <p>
	 *     Hint: you will need {@link Flux#flatMap(Function)} and inner {@link Mono#filter(Predicate)}
	 * </p>
	 */
	@Test
	public void implementAsyncFilteringUsingFlatMap() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words());

		//when
		final Flux<String> filtered = words;

		//then
		filtered
				.as(StepVerifier::create)
				.expectNext("ipsum")
				.expectNextCount(9)
				.verifyComplete();
	}

	/**
	 * TODO filter words using {@link Flux#filterWhen(Function)}
	 * <p>
	 *     Hint: you will also need inner {@link Mono#map(Function)}
	 * </p>
	 */
	@Test
	public void filterWhen() throws Exception {
		//given
		final Flux<String> words = Flux.just(LoremIpsum.words());

		//when
		final Flux<String> filtered = words;

		//then
		filtered
				.as(StepVerifier::create)
				.expectNext("ipsum")
				.expectNextCount(9)
				.verifyComplete();
	}

	/**
	 * Not really async...
	 */
	private Mono<HashCode> asyncSha256(String input) {
		return Mono.fromCallable(() -> sha256(input));
	}

}
