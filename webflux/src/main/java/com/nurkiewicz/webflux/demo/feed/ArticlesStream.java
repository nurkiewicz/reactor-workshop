package com.nurkiewicz.webflux.demo.feed;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class ArticlesStream {

    private final ArticleRepository articleRepository;
    private final FeedAggregator feedAggregator;

    public ArticlesStream(ArticleRepository articleRepository, FeedAggregator feedAggregator) {
        this.articleRepository = articleRepository;
        this.feedAggregator = feedAggregator;
    }

    Flux<Article> newArticles() {
        return feedAggregator
                .articles()
                .filterWhen(onlyNewArticles())
                .flatMap(articleRepository::save);
    }

    @NotNull
    private Function<Article, Publisher<Boolean>> onlyNewArticles() {
        return article -> articleRepository
                .existsById(article.getLink())
                .map(x -> !x);
    }

}
