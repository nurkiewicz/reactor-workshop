package com.nurkiewicz.reactor.domains;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class RealCrawler {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ConcurrentMap<URI, Instant> crawled = new ConcurrentHashMap<>();

	private final Scheduler scheduler = Schedulers.newBoundedElastic(10, 1000, "Crawler");

	public Flux<URI> outgoingLinks(URI uri) {
		return Mono
				.fromCallable(() -> {
					if (crawled.putIfAbsent(uri, Instant.now()) == null) {
						Document doc = Jsoup.connect(uri.toString()).get();
						return doc.select("a");
					} else {
						return null;
					}
				})
				.subscribeOn(scheduler)
				.doOnError(e -> log.warn("Failed to load {}", uri, e))
				.onErrorResume(e -> Mono.empty())
				.flatMapMany(elements -> Flux.fromStream(elements.stream()))
				.map(e -> e.absUrl("href"))
				.filter(StringUtils::isNotEmpty)
				.map(href -> StringUtils.substringBefore(href, "#"))
				.flatMap(s -> Mono.fromCallable(() -> URI.create(s)).onErrorResume(e -> Mono.empty()))
				.filter(u -> !crawled.containsKey(u));

	}

	private Flux<URI> crawl(URI url) {
		return Mono.just(url).expand(this::outgoingLinks);
	}

	public static void main(String[] args) {
		RealCrawler crawler = new RealCrawler();
		crawler
				.crawl(URI.create("https://www.yahoo.com"))
				.delayElements(Duration.ofMillis(500))
				.take(100)
				.doOnNext(url -> log.info(url.toString()))
				.blockLast();
	}

}
