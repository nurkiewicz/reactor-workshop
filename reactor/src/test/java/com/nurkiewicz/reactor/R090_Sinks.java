package com.nurkiewicz.reactor;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static reactor.core.publisher.Sinks.EmitResult.FAIL_TERMINATED;
import static reactor.core.publisher.Sinks.EmitResult.FAIL_ZERO_SUBSCRIBER;
import static reactor.core.publisher.Sinks.EmitResult.OK;

public class R090_Sinks {

	private static final Logger log = LoggerFactory.getLogger(R090_Sinks.class);

	@Test
	public void sinksManyUnicast() throws Exception {
		final Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();
		assertThat(sink.tryEmitNext(1)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(2)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(3)).isEqualTo(OK);
		assertThat(sink.tryEmitComplete()).isEqualTo(OK);

		assertThat(sink.asFlux().collectList().block()).containsExactly(1, 2, 3);

		try {
			sink.asFlux().next().block();
			failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			assertThat(e).hasMessageContaining("allows only a single Subscriber");
		}
	}

	@Test
	public void sinksManyMulticast() throws Exception {
		final Sinks.Many<Integer> sink = Sinks.many().multicast().onBackpressureBuffer();
		assertThat(sink.tryEmitNext(1)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(2)).isEqualTo(OK);
		assertThat(sink.tryEmitComplete()).isEqualTo(OK);
		assertThat(sink.tryEmitNext(4)).isEqualTo(FAIL_TERMINATED);

		assertThat(sink.asFlux().collectList().block()).containsExactly(1, 2);
		assertThat(sink.asFlux().collectList().block()).isEmpty();
	}

	@Test
	public void sinksManyMulticastSmallBuffer() throws Exception {
		final Sinks.Many<Integer> sink = Sinks.many().multicast().onBackpressureBuffer(1);
		assertThat(sink.tryEmitNext(1)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(2)).isEqualTo(FAIL_ZERO_SUBSCRIBER);
		assertThat(sink.tryEmitComplete()).isEqualTo(OK);
		assertThat(sink.tryEmitNext(4)).isEqualTo(FAIL_TERMINATED);

		assertThat(sink.asFlux().collectList().block()).containsExactly(1);
		assertThat(sink.asFlux().collectList().block()).isEmpty();
	}

	@Test
	public void sinksManyReplayAll() throws Exception {
		final Sinks.Many<Integer> sink = Sinks.many().replay().all();
		assertThat(sink.tryEmitNext(1)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(2)).isEqualTo(OK);
		final Flux<Integer> flux1 = sink.asFlux();
		assertThat(sink.tryEmitNext(3)).isEqualTo(OK);
		final Flux<Integer> flux2 = sink.asFlux();
		assertThat(sink.tryEmitComplete()).isEqualTo(OK);

		assertThat(flux1.collectList().block()).containsExactly(1,2,3);
		assertThat(flux2.collectList().block()).containsExactly(1,2,3);
	}

	@Test
	public void sinksManyReplayLatest() throws Exception {
		final Sinks.Many<Integer> sink = Sinks.many().replay().limit(3);
		assertThat(sink.tryEmitNext(1)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(2)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(3)).isEqualTo(OK);
		assertThat(sink.tryEmitNext(4)).isEqualTo(OK);
		sink.asFlux().subscribe(x -> log.info("A: {}", x));
		assertThat(sink.tryEmitNext(5)).isEqualTo(OK);
		sink.asFlux().subscribe(x -> log.info("B: {}", x));
		assertThat(sink.tryEmitComplete()).isEqualTo(OK);
	}

}
