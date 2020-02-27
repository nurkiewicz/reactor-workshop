package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.FeedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
