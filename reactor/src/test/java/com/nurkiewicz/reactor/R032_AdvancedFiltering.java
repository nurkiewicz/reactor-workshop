package com.nurkiewicz.reactor;

import com.google.common.collect.ImmutableList;
import com.nurkiewicz.reactor.samples.*;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.math.BigInteger.ONE;
import static org.assertj.core.api.Assertions.assertThat;

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
				.expectNextCount(2)
				.verifyComplete();
	}

	/**
	 * TODO Implement {@link #dividersOf(BigInteger)}
	 */
	@Test
	public void generateAnyNumberOfBigIntegers() throws Exception {
		assertThat(
				bigIntegers().take(4).collectList().block()
		).containsExactly(ONE, BigInteger.TWO, BigInteger.valueOf(3), BigInteger.valueOf(4));

		assertThat(
				bigIntegers()
						.map(BigInteger::intValue)
						.skip(1000)
						.take(5)
						.collectList()
						.block()
		).containsExactly(1001, 1002, 1003, 1004, 1005);
	}

	/**
	 * TODO Generate BigIntegers, starting from 1.
	 * <p>
	 * Use {@link Flux#generate(Consumer)} with state starting at {@link BigInteger#ONE}
	 * </p>
	 */
	private Flux<BigInteger> bigIntegers() {
		return Flux
				.generate(
						() -> ONE,
						(state, sink) -> {
							sink.next(state);
							return state.add(ONE);
						});
	}

	/**
	 * TODO Generate all numbers from 2 to (sqrt(x))
	 */
	@Test
	public void generateDividers() throws Exception {
		assertThat(dividersBlocking(2)).isEmpty();
		assertThat(dividersBlocking(3)).isEmpty();
		assertThat(dividersBlocking(4)).containsExactly(2L);
		assertThat(dividersBlocking(5)).containsExactly(2L);
		assertThat(dividersBlocking(6)).containsExactly(2L);
		assertThat(dividersBlocking(7)).containsExactly(2L);
		assertThat(dividersBlocking(8)).containsExactly(2L);
		assertThat(dividersBlocking(9)).containsExactly(2L, 3L);
		assertThat(dividersBlocking(15)).containsExactly(2L, 3L);
		assertThat(dividersBlocking(16)).containsExactly(2L, 3L, 4L);
		assertThat(dividersBlocking(17)).containsExactly(2L, 3L, 4L);
		assertThat(dividersBlocking(24)).containsExactly(2L, 3L, 4L);
		assertThat(dividersBlocking(25)).containsExactly(2L, 3L, 4L, 5L);
		assertThat(dividersBlocking(26)).containsExactly(2L, 3L, 4L, 5L);
		assertThat(dividersBlocking(99)).containsExactly(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
		assertThat(dividersBlocking(100)).containsExactly(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
		assertThat(dividersBlocking(101)).containsExactly(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
	}

	/**
	 * Do not change this method
	 */
	private List<Long> dividersBlocking(long x) {
		return dividersOf(BigInteger.valueOf(x))
				.map(BigInteger::longValueExact)
				.collectList()
				.block();
	}

	/**
	 * TODO Dividers are numbers starting at 2 up to sqrt(x)
	 * <p>
	 * Hint: {@link BigInteger#sqrt()}, {@link Flux#takeWhile(Predicate)}
	 * </p>
	 */
	private Flux<BigInteger> dividersOf(BigInteger x) {
		System.out.println("sqrt start");
		final BigInteger max = x.sqrt();
		System.out.println("sqrt end");
		return bigIntegers()
				.takeWhile(d -> d.compareTo(max) <= 0)
				.skip(1);
	}

	@Test
	public void isSmallIntegerPrime() throws Exception {
		Primes.PRIMES
				.forEach(prime ->
						assertThat(isPrime(BigInteger.valueOf(prime)).block())
								.as(prime + " is a prime number")
								.isTrue()
				);
	}

	@Test
	public void smallIntegerComposite() throws Exception {
		Primes.COMPOSITE
				.forEach(prime ->
						assertThat(isPrime(BigInteger.valueOf(prime)).block())
								.as(prime + " is a composite number")
								.isFalse()
				);
	}

	@Test
	public void isBigIntegerPrime() {
		//given
		final BigInteger notPrime = Primes.LARGE.subtract(ONE);
		final BigInteger prime = Primes.LARGE;

		//then
		assertThat(isPrime(notPrime).block()).isFalse();
		assertThat(isPrime(prime).block()).isTrue();
	}

	/**
	 * TODO Does all ({@link Flux#all(Predicate)}) number divide <code>x</code>
	 * @see Flux#all(Predicate)
	 * @see Flux#any(Predicate)
	 */
	private Mono<Boolean> isPrime(BigInteger x) {
		return dividersOf(x)
				.all(div -> indivisible(div, x));

	}

	private boolean indivisible(BigInteger div, BigInteger x) {
		return x.mod(div).compareTo(BigInteger.ZERO) > 0;
	}


}
