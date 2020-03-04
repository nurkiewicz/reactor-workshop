package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.feed.synd.SyndEntry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        Duration randDuration = Duration.ofMillis(ThreadLocalRandom.current().nextInt(10_000, 30_000));
        return Flux
                .interval(randDuration)
                .flatMap(i -> fetchEntries(outline.getXmlUrl()))
                .flatMap(this::toArticle);
    }

    private Mono<Article> toArticle(SyndEntry entry) {
        if (entry.getPublishedDate() == null) {
            return Mono.empty();
        }
        return Mono
                .fromCallable(() ->
                        new Article(URI.create(entry.getLink()), entry.getPublishedDate().toInstant(), entry.getTitle())
                )
                .doOnError(e -> log.warn("Unable to create article from {}", entry, e))
                .onErrorResume(e -> Mono.empty());
    }

    @NotNull
    private Flux<SyndEntry> fetchEntries(String url) {
        return feedReader
                .fetch(url)
                .doOnSubscribe(s -> log.info("Fetching entries from {}", url))
                .doOnError(e -> log.warn("Failed to fetch {}", url, e))
                .onErrorResume(e -> Flux.empty());
    }
}
