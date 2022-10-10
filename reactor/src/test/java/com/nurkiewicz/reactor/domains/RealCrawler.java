package com.nurkiewicz.reactor.domains;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class RealCrawler implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ConcurrentMap<URI, Instant> crawled = new ConcurrentHashMap<>();

	private final Scheduler scheduler = Schedulers.newBoundedElastic(100, 1000, "Crawler");

	public Flux<URI> outgoingLinks(URI uri) {
		return Mono
				.just(uri)
				.flatMap(this::extractAhrefElements)
				.doOnError(e -> log.warn("Failed to load {}", uri, e))
				.onErrorResume(e -> Mono.empty())
				.doOnNext(x -> log.info("Crawled {}", uri))
				.flatMapMany(elements -> Flux.fromStream(elements.stream()))
				.transform(this::extractUrls);
	}

	private Mono<Elements> extractAhrefElements(URI u) {
		return Mono
				.fromCallable(() -> {
					if (crawled.putIfAbsent(u, Instant.now()) == null) {
						return Jsoup.connect(u.toString()).get().select("a");
					} else {
						return null;
					}
				})
				.subscribeOn(scheduler);
	}

	private Flux<URI> extractUrls(Flux<Element> aElement) {
		return aElement
				.map(e -> e.absUrl("href"))
				.filter(StringUtils::isNotEmpty)
				.map(href -> StringUtils.substringBefore(href, "#"))
				.flatMap(s -> Mono
						.fromCallable(() -> URI.create(s))
						.doOnError(e -> log.warn("Failed to parse {} ({})", s, e.toString()))
						.onErrorResume(e -> Mono.empty())
				).filter(uri -> uri.getScheme().equals("http") || uri.getScheme().equals("https"));
	}

	private Flux<URI> crawl(URI url) {
		return Mono.just(url)
				.expand(this::outgoingLinks);
	}

	public static void main(String[] args) {
		try (RealCrawler realCrawler = new RealCrawler()) {
			realCrawler
					.crawl(URI.create("https://nurkiewicz.com"))
					.blockLast();
		}
	}

	@Override
	public void close()  {
		scheduler.dispose();
	}
}
