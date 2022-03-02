package com.nurkiewicz.reactor.domains;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RealCrawler {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ConcurrentMap<URI, Instant> crawled =
			new ConcurrentHashMap<>();

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
//				.subscribeOn(scheduler)
				.doOnError(e -> log.warn("Failed to load {}", uri, e))
				.onErrorResume(e -> Mono.empty())
				.flatMapMany(elements ->
						Flux.fromStream(elements.stream())
								.map(e -> e.absUrl("href"))
								.map(URI::create));

	}

	private Flux<URI> crawl(URI url) {
		return Mono.just(url).expand(this::outgoingLinks);
	}

	public static void main(String[] args) {
		RealCrawler crawler = new RealCrawler();
		crawler
				.crawl(URI.create("https://www.google.com"))
				.take(10000)
				.doOnNext(url -> log.info(url.toString()))
				.blockLast();
	}

}
