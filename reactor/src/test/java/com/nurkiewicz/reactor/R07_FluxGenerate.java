package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.math.BigInteger.ONE;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R07_FluxGenerate {

	private static final Logger log = LoggerFactory.getLogger(R07_FluxGenerate.class);

	/**
	 * Backpressure-aware
	 */
	@Test
	public void generateRandomStreamOfNumbers() throws Exception {
		//given
		final Flux<Float> randoms = Flux
				.generate(sink ->
				{
					final float rand = ThreadLocalRandom.current().nextFloat();
					log.debug("Returning {}", rand);
					sink.next(rand);
				});

		//when
		final Flux<Float> twoRandoms = randoms
				.take(2);

		//then
		twoRandoms
				.as(StepVerifier::create)
				.expectNextMatches(x -> x >= 0 && x < 1)
				.expectNextMatches(x -> x >= 0 && x < 1)
				.verifyComplete();
	}

	/**
	 * TODO Generate a stream of random <code>true</code> and <code>false</code>
	 */
	@Test
	public void generateRandomBooleans() throws Exception {
		//given
		final int total = 1_000;
		final Flux<Boolean> randoms = Flux
				.generate(sink -> sink.next(true));

		//when
		final Flux<Boolean> thousandBooleans = randoms.take(total);

		//then
		final Long trues = thousandBooleans.filter(x -> x == true).count().block();
		final Long falses = thousandBooleans.filter(x -> x == false).count().block();
		assertThat(trues).isBetween(400L, 600L);
		assertThat(falses).isBetween(400L, 600L);

		//and
		//TODO Uncomment line below. Why does it fail? Fix it
//		assertThat(trues + falses).isEqualTo(total);
	}

	@Test
	public void statefulGenerate() throws Exception {
		//given
		final Flux<Integer> naturals = Flux.generate(() -> 0, (state, sink) -> {
			sink.next(state);
			return state + 1;
		});

		//when
		final Flux<Integer> three = naturals
				.take(3);

		//then
		three
				.as(StepVerifier::create)
				.expectNext(0)
				.expectNext(1)
				.expectNext(2)
				.verifyComplete();
	}

	/**
	 * TODO Implement stream of powers of two (1, 2, 4, 8, 16)
	 */
	@Test
	public void powersOfTwo() throws Exception {
		//given
		final Flux<BigInteger> naturals = Flux.generate(() -> ONE, (state, sink) -> {
			//TODO
			return state;
		});

		//when
		final Flux<BigInteger> three = naturals
				.skip(10)
				.take(3);

		//then
		three
				.as(StepVerifier::create)
				.expectNext(BigInteger.valueOf(1024))
				.expectNext(BigInteger.valueOf(2048))
				.expectNext(BigInteger.valueOf(4096))
				.verifyComplete();
	}

	/**
	 * TODO Generate Fibonacci sequence. Hints:
	 * <p><ul>
	 * <li>Your initial state is a pair <code>(0L, 1L)</code> ({@link Tuples#of(Object, Object)})</li>
	 * <li>The first item is the sum <code>0 + 1</code></li>
	 * <li>New state is the right value from a pair and a sum: <code>(a, b) -> (b, a + b)</code></li>
	 * </ul>
	 * </p>
	 * <table>
	 * <tr><td>(0, 1)</td> <td>1</td></tr>
	 * <tr><td>(1, 1)</td> <td>2</td></tr>
	 * <tr><td>(1, 2)</td> <td>3</td></tr>
	 * <tr><td>(2, 3)</td> <td>5</td></tr>
	 * <tr><td>(3, 5)</td> <td>8</td></tr>
	 * </table>
	 */
	@Test
	public void fibonacci() throws Exception {
		//given
		final Flux<Long> fib = Flux.generate(
				() -> null/* TODO initial state */,
				(p, sink) -> {
					//TODO
					return p;
				});

		//when
		final Flux<Long> first10 = fib.take(10);

		//then
		first10
				.as(StepVerifier::create)
				.expectNext(1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L, 55L, 89L)
				.verifyComplete();
	}

	/**
	 * TODO Compute 1000th Fibonacci using {@link BigInteger}.
	 * <p>Hint: start by copy-pasting solution above</p>
	 *
	 * @see <a href="https://www.bigprimes.net/archive/fibonacci/999/">table</a>
	 */
	@Test
	public void compute1000thFibonacciUsingBigInteger() throws Exception {
		//given
		final Flux<BigInteger> fib = Flux.generate(
				() -> null /* TODO initial state*/,
				(p, sink) -> p);

		//when
		final Mono<BigInteger> thousandth = fib
				.skip(997)  //off-by-one (two) - True Fibonacci starts with 0, 1, 1, 2
				.next();

		//then
		thousandth
				.as(StepVerifier::create)
				.expectNext(new BigInteger("26863810024485359386146727202142923967616609318986952340123175997617981700247881689338369654483356564191827856161443356312976673642210350324634850410377680367334151172899169723197082763985615764450078474174626"))
				.verifyComplete();
	}

	/**
	 * TODO Fix exercise above by prepending first two elements (0 and 1) in the beginning
	 * <p>Hint: preferrably use {@link Flux#startWith(Object[])}</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void prependFirstTwoFibonacci() throws Exception {
		//given
		final Flux<BigInteger> fib = Flux.generate(
				() -> null /* TODO initial state */,
				(p, sink) -> p);

		//when
		final Mono<BigInteger> thousandth = fib
				//TODO one extra operator here
				.skip(999)
				.next();

		//then
		thousandth
				.as(StepVerifier::create)
				.expectNext(new BigInteger("26863810024485359386146727202142923967616609318986952340123175997617981700247881689338369654483356564191827856161443356312976673642210350324634850410377680367334151172899169723197082763985615764450078474174626"))
				.verifyComplete();
	}

	/**
	 * TODO Read a file line-by-line
	 * <p>Hints:
	 * <ul>
	 * <li>Your initial state should be a {@link BufferedReader}, see {@link #open(String)} helper method</li>
	 * <li>remember to close the file (third argument to {@link Flux#generate(Callable, BiFunction, Consumer)}</li>
	 * </ul>
	 * </p>
	 */
	@Test
	public void readingFileLineByLine() throws Exception {
		//given
		Flux<String> lines = Flux
				.generate(
						() -> (BufferedReader) null /* TODO initial state, read /logback-test.xml */,
						(reader, sink) -> {
							readLine(reader, sink);
							return reader;
						} /* TODO something here */);

		//when
		final Flux<String> first10lines = lines.take(10);

		//then
		final List<String> list = first10lines
				.collectList()
				.block();

		assertThat(list).hasSize(10);
	}

	@Test
	public void makeSureWeUnderstandEndOfFile() throws Exception {
		//given
		Flux<String> lines = Flux
				.generate(
						() -> (BufferedReader) null /* TODO initial state, read /logback-test.xml */,
						(reader, sink) -> {
							readLine(reader, sink);
							return reader;
						},
						this::closeQuitely);

		//when

		//then
		lines
				.as(StepVerifier::create)
				.expectNextCount(12)
				.verifyComplete();
	}

	private void closeQuitely(AutoCloseable closeable) {
		try {
			log.info("Closing {}", closeable);
			closeable.close();
		} catch (Exception e) {
			log.warn("Unable to close {}", closeable, e);
		}
	}

	private void readLine(BufferedReader file, SynchronousSink<String> sink) {
		//TODO Implement, remember about end of file,
	}

	private BufferedReader open(String path) {
		return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
	}

}
