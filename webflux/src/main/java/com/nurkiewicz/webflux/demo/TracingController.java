package com.nurkiewicz.webflux.demo;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class TracingController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final WebClient webClient;
	private final Scheduler instrumentedScheduler;

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
		return tracing()
				.subscribeOn(Schedulers.boundedElastic());
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
