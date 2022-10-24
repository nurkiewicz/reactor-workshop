package com.nurkiewicz.reactor;

import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <a href="https://projectreactor.io/docs/core/release/reference/#context">Reference</a>
 */
public class R40_Context {

	private static final Logger log = LoggerFactory.getLogger(R40_Context.class);

	private final String KEY = "KEY";

	@Test
	@Ignore
	public void threadLocalIsBroken() {
		ThreadLocal<String> txId = new ThreadLocal<>();
		Mono<String> mono = Mono
				.fromCallable(txId::get)
				.subscribeOn(Schedulers.boundedElastic());

		txId.set("Hello");
		assertThat(mono.block()).isEqualTo("Hello");
	}

	@Test
	public void helloContext() {
		Mono<String> mono = Mono.just("Hello")
				.flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get(KEY))))
				.contextWrite(ctx -> ctx.put(KEY, "World"));
		assertThat(mono.block()).isEqualTo("Hello World");
	}

	@Test
	public void passingContext() {
		Mono<String> mono = Mono.just("")
				.flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + "1" + ctx.getOrDefault(KEY, "X "))))
				.contextWrite(ctx -> ctx.put(KEY, "A "))

				.flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + "2" + ctx.getOrDefault(KEY, "Y "))))
				.contextWrite(ctx -> ctx.put(KEY, "B "))

				.flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + "3" + ctx.getOrDefault(KEY, "Z "))));

		assertThat(mono.block()).isEqualTo("1A 2B 3Z ");
	}

	/**
	 * TODO Make <code>jwt</code> value available to {@link #query(int, JwtToken)} method.
	 * @see Flux#contextWrite(Function)
	 */
	@Test
	public void passTraceIdToChildren() {
		JwtToken jwt = new JwtToken("adam");
		Flux<String> flux = Flux.just(1, 2, 3)
				.flatMap(id -> Mono.deferContextual(ctx -> query(id, ctx.get(KEY))))
				.contextWrite(ctx -> ctx.put(KEY, jwt));

		assertThat(flux.collectList().block()).containsExactly(
				"Response for " + 1 + " from " + "adam",
				"Response for " + 2 + " from " + "adam",
				"Response for " + 3 + " from " + "adam"
		);
	}


	Mono<String> query(int id, JwtToken jwtToken) {
		return Mono.just("Response for " + id + " from " + jwtToken)
				.subscribeOn(Schedulers.boundedElastic());
	}

}

class JwtToken {
	private final String name;

	JwtToken(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}