package com.nurkiewicz.webflux.demo.feed;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    private final ArticlesStream articlesStream;

    public ArticlesController(ArticlesStream articlesStream) {
        this.articlesStream = articlesStream;
    }

    /**
     * TODO (6) Return newest articles
     */
    @GetMapping("/newest/{limit}")
    Flux<Article> newest(@PathVariable int limit) {
        return Flux.empty();
    }

    /**
     * TODO (8) Create an SSE stream of newest articles.
     * Possible solutions:
     * <ol>
     *     <li>articleRepository.save() inserts into sink, then sink.asFlux()</li>
     *     <li>@Tailable on MongoDB with capped collection</li>
     *     <li>insert into queue (Redis? Kafka?), fetch from this queue in this endpoint</li>
     * </ol>
     */
    @GetMapping(value = "/newest-stream", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Article> streamNew() {
        return Flux.empty();
    }

}
