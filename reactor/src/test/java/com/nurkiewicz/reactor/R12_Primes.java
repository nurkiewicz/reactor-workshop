package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.samples.Primes;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R12_Primes {

	private static final Logger log = LoggerFactory.getLogger(R12_Primes.class);

	/**
	 * TODO Generate all numbers from 2 to (sqrt(x))
	 */
	@Test
	public void generateDividers() throws Exception {
		assertThat(dividersBlocking(2)).isEmpty();
		assertThat(dividersBlocking(3)).isEmpty();
		assertThat(dividersBlocking(4)).containsExactly(2);
		assertThat(dividersBlocking(5)).containsExactly(2);
		assertThat(dividersBlocking(6)).containsExactly(2);
		assertThat(dividersBlocking(7)).containsExactly(2);
		assertThat(dividersBlocking(8)).containsExactly(2);
		assertThat(dividersBlocking(9)).containsExactly(2, 3);
		assertThat(dividersBlocking(15)).containsExactly(2, 3);
		assertThat(dividersBlocking(16)).containsExactly(2, 3, 4);
		assertThat(dividersBlocking(17)).containsExactly(2, 3, 4);
		assertThat(dividersBlocking(24)).containsExactly(2, 3, 4);
		assertThat(dividersBlocking(25)).containsExactly(2, 3, 4, 5);
		assertThat(dividersBlocking(26)).containsExactly(2, 3, 4, 5);
		assertThat(dividersBlocking(99)).containsExactly(2, 3, 4, 5, 6, 7, 8, 9);
		assertThat(dividersBlocking(100)).containsExactly(2, 3, 4, 5, 6, 7, 8, 9, 10);
		assertThat(dividersBlocking(101)).containsExactly(2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	/**
	 * Do not change this method
	 */
	private List<Integer> dividersBlocking(long x) {
		return dividersOf(x)
				.collectList()
				.block();
	}

	/**
	 * TODO Dividers are numbers starting at 2 up to sqrt(x)
	 */
	private Flux<Integer> dividersOf(long x) {
		final int sqrt = (int) Math.sqrt(x);
		return Flux.range(2, sqrt - 1);
	}

	/**
	 * TODO Check if number is prime by implementing {@link #isPrime(long)}
	 */
	@Test
	public void isSmallIntegerPrime() throws Exception {
		Primes.PRIMES
				.forEach(prime ->
						assertThat(isPrime(prime).block())
								.as(prime + " is a prime number")
								.isTrue()
				);
	}

	@Test
	public void smallIntegerComposite() throws Exception {
		Primes.COMPOSITE
				.forEach(prime ->
						assertThat(isPrime(prime).block())
								.as(prime + " is a composite number")
								.isFalse()
				);
	}

	@Test
	public void isBigIntegerPrime() {
		//given
		final long notPrime = Primes.LARGE - 1;
		final long prime = Primes.LARGE;

		//then
		assertThat(isPrime(notPrime).block()).isFalse();
		assertThat(isPrime(prime).block()).isTrue();
	}

	/**
	 * TODO Does all ({@link Flux#all(Predicate)}) number divide <code>x</code>
	 * @see Flux#all(Predicate)
	 * @see Flux#any(Predicate)
	 */
	private Mono<Boolean> isPrime(long x) {
		return dividersOf(x)
				.all(div -> x % div != 0);
	}

}
