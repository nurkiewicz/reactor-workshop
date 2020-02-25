package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.FeedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * <ul>
 *	<li>Use {@link WebClient} instead of {@link HttpURLConnection}.</li>
 *	<li>Handle errors and 301 redirect</li>
 *	<li>Publish SSE stream of articles</li>
 *	<li>Polling for new articles periodically</li>
 *	<li>Store articles in the database (use e.g. {@link ReactiveMongoRepository})</li>
 *	<li>Create endpoints for browsing (e.g. most recent, about something, by author...)</li>
 *	<li>Make a simple front-end</li>
 * </ul>
 */
@Component
public class FeedAggregator {

    private static final Logger log = LoggerFactory.getLogger(FeedAggregator.class);

    private final OpmlReader opmlReader;
    private final FeedReader feedReader;

    public FeedAggregator(OpmlReader opmlReader, FeedReader feedReader) {
        this.opmlReader = opmlReader;
        this.feedReader = feedReader;
    }

    @PostConstruct
    public void init() throws IOException, FeedException {
        opmlReader
                .allFeeds()
                .map(Outline::getXmlUrl)
                .flatMap(this::fetchEntries)
                .subscribe(e ->
                        log.info("{}: {} at {}", e.getPublishedDate(), e.getTitle(), e.getLink())
                );
    }

    @NotNull
    private Flux<SyndEntry> fetchEntries(String url) {
        return feedReader
                .fetch(url)
                .doOnError(e -> log.warn("Unable to download {}", url, e))
                .onErrorResume(e -> Flux.empty());
    }
}
