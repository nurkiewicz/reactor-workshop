package com.nurkiewicz.webflux.demo;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;


@RestController
class ReactorController {

	private static final Logger log = LoggerFactory.getLogger(ReactorController.class);

	private final WebClient webClient;

	ReactorController(WebClient webClient) {
		this.webClient = webClient;
	}

	@GetMapping("/hello")
	Mono<String> hello() {
		return Mono
				.just(Instant.now())
				.delayElement(Duration.ofMillis(500))
				.map(Instant::toString);
	}

	@GetMapping("/fast")
	Mono<String> fast() {
		return Mono
				.just(Instant.now())
				.map(Instant::toString);
	}

	@GetMapping(value = "/stream", produces = TEXT_EVENT_STREAM_VALUE)
	Flux<Ping> stream() {
		return Flux
				.interval(Duration.ofMillis(500))
				.map(x -> new Ping(x, Instant.now()))
				.doOnCancel(() -> log.info("Interrupted by client"));
	}

	@GetMapping("/error/immediate")
	Flux<String> errorImmediate() {
		throw new RuntimeException("Opps :-(");
	}

	@GetMapping("/error/async")
	Flux<String> errorAsync() {
		return Flux
				.<String>error(new RuntimeException("Delayed"))
				.delayElements(Duration.ofMillis(500));
	}

	@GetMapping(value = "/cached")
	Mono<ResponseEntity<Book>> cached() {
		return Mono.fromCallable(() ->
						new Book("Tolkien", "Lord Of The Rings"))
				.map(book ->
						ResponseEntity
								.ok()
								.contentType(APPLICATION_JSON)
								.cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
								.eTag(String.valueOf(book.hashCode()))
								.body(book)
				);
	}

	@GetMapping("/proxy")
	Mono<String> exampleProxy() {
		return webClient
				.get()
				.uri("http://example.com")
				.retrieve()
				.bodyToMono(String.class);
	}

	@GetMapping("/leak")
	Mono<String> leak() {
		return webClient
				.get()
				.uri("http://example.com")
				.exchange()
//				.flatMap(response -> response.bodyToMono(Void.class))
				.map(response -> "");
	}

}

class Ping {

	private final long seqNo;
	private final Instant timestamp;

	Ping(long seqNo, Instant timestamp) {
		this.seqNo = seqNo;
		this.timestamp = timestamp;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public long getSeqNo() {
		return seqNo;
	}
}

class Book {

	private final String author;
	private final String title;

	public Book(String author, String title) {
		this.author = author;
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}
}