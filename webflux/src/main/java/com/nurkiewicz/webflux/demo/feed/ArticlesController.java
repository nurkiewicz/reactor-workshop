package com.nurkiewicz.webflux.demo.feed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    private final ArticlesStream articlesStream;

    public ArticlesController(ArticlesStream articlesStream) {
        this.articlesStream = articlesStream;
    }

    @GetMapping("/newest/{limit}")
    Flux<Article> newest(@PathVariable int limit) {
        return Flux.empty();
    }

}
