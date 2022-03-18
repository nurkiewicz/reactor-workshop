package com.nurkiewicz.webflux.demo;


import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/blocking")
class BlockingController {

	private static final Logger log = LoggerFactory.getLogger(BlockingController.class);

	@GetMapping(value = "/sync")
	Book sync() throws InterruptedException {
		TimeUnit.SECONDS.sleep(2);
		return new Book("Tolkien", "Hobbit");
	}

	@GetMapping(value = "/mono")
	Mono<Book> mono() {
		return Mono.fromCallable(this::sync);
	}

	@GetMapping(value = "/subscribeOn")
	Mono<Book> subscribeOn() {
		return mono()
				.subscribeOn(Schedulers.boundedElastic());
	}

}

