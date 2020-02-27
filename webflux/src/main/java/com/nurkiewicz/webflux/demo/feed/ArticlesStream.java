package com.nurkiewicz.webflux.demo.feed;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ArticlesStream {

    private final ArticleRepository articleRepository;
    private final FeedAggregator feedAggregator;

    public ArticlesStream(ArticleRepository articleRepository, FeedAggregator feedAggregator) {
        this.articleRepository = articleRepository;
        this.feedAggregator = feedAggregator;
    }

    Flux<Article> newArticles() {
        return Flux.empty();
    }

}
