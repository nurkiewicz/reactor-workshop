package com.nurkiewicz.webflux.demo.feed;

import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;

@Component
public class ArticlesStream {

    private final ArticleRepository articleRepository;
    private final FeedAggregator feedAggregator;

    public ArticlesStream(ArticleRepository articleRepository, FeedAggregator feedAggregator) {
        this.articleRepository = articleRepository;
        this.feedAggregator = feedAggregator;
    }

    /**
     * TODO (7) Create an infinite stream of new articles
     * @return
     */
    Flux<Article> newArticles() {
        return Flux.empty();
    }

}
