package com.nurkiewicz.reactor.pagehit;

import com.devskiller.jfairy.Fairy;
import com.google.common.base.MoreObjects;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PageHit {
	private final Instant timestamp;
	private final Country country;
	private final URI url;

	private PageHit(Instant timestamp, Country country, URI url) {
		this.timestamp = timestamp;
		this.country = country;
		this.url = url;
	}

	public static PageHit random(Fairy fairy) {
		try {
			return new PageHit(
					Instant.ofEpochMilli(Schedulers.elastic().now(TimeUnit.MILLISECONDS)),
					Country.random(),
					new URI(randomUrl(fairy)));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static String randomUrl(Fairy fairy) {
		return "http://" + fairy.textProducer().word(1) + ".com/" + fairy.textProducer().word(1) + ".html";
	}

	public Country getCountry() {
		return country;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public URI getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("timestamp", timestamp)
				.add("country", country)
				.add("url", url)
				.toString();
	}
}