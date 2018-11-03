package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;

@Ignore
public class R060_TransformCompose {

	private static final Logger log = LoggerFactory.getLogger(R060_TransformCompose.class);

	@Test
	public void transformGroupsManyOperators() throws Exception {
		//given
		Flux
				.interval(ofNanos(1))
				.window(ofSeconds(1))
				.flatMap(Flux::count)
				.index()
				.doOnNext(t -> log.info("{}: {}/s", t.getT1(), t.getT2()))
				.subscribe();

		//when

		//then
		TimeUnit.SECONDS.sleep(5);
	}

	@Test
	public void poorReuse() throws Exception {
		final Flux<Long> input = Flux.interval(ofNanos(1));
		final Flux<Tuple2<Long, Long>> out = countPerSecond(input);
	}

	@Test
	public void transform() throws Exception {
		final Flux<Tuple2<Long, Long>> out = Flux
				.interval(ofNanos(1))
				.transform(this::countPerSecond)
				.take(3);

		out.subscribe();
		out.subscribe();
	}

	@Test
	public void compose() throws Exception {
		final Flux<Tuple2<Long, Long>> out = Flux
				.interval(ofNanos(1))
				.compose(this::countPerSecond)
				.take(3);

		out.subscribe();
		out.subscribe();
	}

	<T> Flux<Tuple2<Long, Long>> countPerSecond(Flux<T> input) {
		log.info("Creating transformation");
		return input
				.window(ofSeconds(1))
				.flatMap(Flux::count)
				.index()
				.doOnNext(t -> log.info("{}: {}/s", t.getT1(), t.getT2()));
	}

}
