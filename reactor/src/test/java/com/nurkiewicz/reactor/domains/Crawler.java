package com.nurkiewicz.reactor.domains;

import com.nurkiewicz.reactor.samples.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.Semaphore;

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

}
