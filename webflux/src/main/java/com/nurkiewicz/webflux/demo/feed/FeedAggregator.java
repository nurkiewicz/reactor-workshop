package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.feed.synd.SyndEntry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FeedAggregator {

    private static final Logger log = LoggerFactory.getLogger(FeedAggregator.class);

    private final OpmlReader opmlReader;
    private final FeedReader feedReader;

    public FeedAggregator(OpmlReader opmlReader, FeedReader feedReader) {
        this.opmlReader = opmlReader;
        this.feedReader = feedReader;
    }

    public Flux<Article> articles() {
        return opmlReader
                .allFeeds()
                .flatMap(this::fetchPeriodically);
    }

    private Flux<Article> fetchPeriodically(Outline outline) {
        Duration randSeconds = Duration.ofSeconds(ThreadLocalRandom.current().nextInt(10, 20));
        return Flux
                .interval(randSeconds)
                .flatMap(i -> fetchEntries(outline.getXmlUrl()))
                .map(this::toArticle);
    }

    private Article toArticle(SyndEntry entry) {
        return new Article(URI.create(entry.getLink()), entry.getPublishedDate().toInstant(), entry.getTitle());
    }

    @NotNull
    private Flux<SyndEntry> fetchEntries(String url) {
        return feedReader
                .fetch(url)
                .doOnError(e -> log.warn("Unable to download {}", url, e))
                .onErrorResume(e -> Flux.empty());
    }
}
