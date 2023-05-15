package com.nurkiewicz.webflux.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.lang.invoke.MethodHandles;

/**
 * Testing (run simple web server in the background, like <code>nc -kl 8040</code>:
 * <pre>
 *     http -v :8080/tracing/proxy X-B3-TraceId:abc X-B3-SpanId:def
 * </pre>
 */
@RestController
public class TracingController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final WebClient webClient;
	private final Scheduler instrumentedScheduler;
	private final Scheduler manual = Schedulers.newBoundedElastic(10, 100, "Manual");

	public TracingController(WebClient webClient, Scheduler instrumentedScheduler) {
		this.webClient = webClient;
		this.instrumentedScheduler = instrumentedScheduler;
	}

	@GetMapping("/tracing/sync")
	Mono<String> tracing() {
		log.info("In method tracing");
		return Mono.fromCallable(() -> {
			log.info("Doing hard work");
			return "abc";
		});
	}

	@GetMapping("/tracing/async-plain")
	Mono<String> tracingAsyncPlain() {
		log.info("In method tracingAsyncPlain");
		return tracing().subscribeOn(manual);
	}

	@GetMapping("/tracing/async-instrumented")
	Mono<String> tracingAsyncInstrumented() {
		log.info("In method tracingAsyncInstrumented");
		return tracing()
				.subscribeOn(instrumentedScheduler);
	}

	@GetMapping("/tracing/proxy")
	Mono<String> tracingProxy() {
		return webClient
				.get()
				.uri("http://localhost:8040")
				.retrieve()
				.bodyToMono(String.class)
				.doOnNext(n -> log.debug("Got response {}", n));
	}

}
