package com.nurkiewicz.webflux.demo.feed;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Function;

@Component
public class ArticlesStream {

    private static final Logger log = LoggerFactory.getLogger(ArticlesStream.class);

    private final ArticleRepository articleRepository;
    private final FeedAggregator feedAggregator;

    public ArticlesStream(ArticleRepository articleRepository, FeedAggregator feedAggregator) {
        this.articleRepository = articleRepository;
        this.feedAggregator = feedAggregator;
    }

    @PostConstruct
    void checkForNewArticles() {
        feedAggregator
                .articles()
                .filterWhen(onlyNewArticles())
                .flatMap(articleRepository::save)
                .doOnNext(a -> log.debug("Saved {}", a))
                .subscribe(
                        x -> {},
                        e -> log.error("Fatal error when periodically fetching articles", e)
                );
    }

    @NotNull
    private Function<Article, Publisher<Boolean>> onlyNewArticles() {
        return article -> articleRepository
                .existsById(article.getLink())
                .doOnNext(exists -> {
                    if (exists) {
                        log.trace("Article {} already found in the database, skipping", article);
                    }
                })
                .map(x -> !x);
    }

}
