package com.nurkiewicz.reactor.domains;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.Semaphore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.nurkiewicz.reactor.samples.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Crawler {

	public static final int MAX_CRAWLERS = 50;

	private static final Logger log = LoggerFactory.getLogger(Crawler.class);

	public static Html crawlBlocking(Domain domain) {
		final URL url = domain.getUrl();
		log.info("About to download {}", url);
		Sleeper.sleepRandomly(Duration.ofMillis(1_000));
		log.info("Returning HTML of {}", url);
		return new Html("<html><title>" + url + "</title></html>");
	}


	private static final Semaphore concLimit = new Semaphore(MAX_CRAWLERS);

	public static Html crawlThrottled(Domain domain) {
		if (concLimit.tryAcquire()) {
			try {
				return crawlBlocking(domain);
			} finally {
				concLimit.release();
			}
		} else {
			throw new RuntimeException("Too many concurrent crawlers: " + MAX_CRAWLERS);
		}
	}

	public static Mono<Html> crawlAsync(Domain domain) {
		return Mono
				.fromCallable(() -> crawlBlocking(domain))
				.subscribeOn(Schedulers.boundedElastic());
	}


	/**
	 * TODO Implement by returning all outgoing links. Use {@link #OUTGOING} map.
	 * @see Mono#justOrEmpty(Object)
	 */
	public static Flux<URI> outgoingLinks(URI url) {
		return Flux.empty();
	}

	private static final ImmutableMap<URI, ImmutableList<URI>> OUTGOING = links();

	private static ImmutableMap<URI, ImmutableList<URI>> links() {
		try {
			return ImmutableMap.<URI, ImmutableList<URI>>builder()
					.put(new URI("https://google.com"), ImmutableList.of(
							new URI("https://abc.xyz/"),
							new URI("https://gmail.com"),
							new URI("https://maps.google.com")
					))
					.put(new URI("https://abc.xyz/"), ImmutableList.of(
							new URI("https://abc.xyz/investor/")
					))
					.put(new URI("https://gmail.com"), ImmutableList.of(
							new URI("https://mail.google.com/mail/u/0"),
							new URI("https://mail.google.com/new")
					))
					.put(new URI("https://maps.google.com"), ImmutableList.of(
							new URI("https://www.google.com/maps/place/Warszawa,+Polska"),
							new URI("https://www.google.com/maps/dir/Warszawa,+Polska"),
									new URI("https://www.google.com/maps/search/Restaurants/")
											))
					.put(new URI("https://abc.xyz/investor/"), ImmutableList.of(
							new URI("https://abc.xyz/investor/other/board/"),
							new URI("https://abc.xyz/investor/other/code-of-conduct/")
					))
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
