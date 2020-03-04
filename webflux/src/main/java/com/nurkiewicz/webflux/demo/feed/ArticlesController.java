package com.nurkiewicz.webflux.demo.feed;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    private final ArticleRepository articleRepository;

    public ArticlesController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/newest/{limit}")
    Flux<Article> newest(@PathVariable int limit) {
        return articleRepository
                .findAll(Sort.by(Sort.Order.desc("publishedDate")))
                .take(limit);
    }

}
