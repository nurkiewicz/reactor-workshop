package com.nurkiewicz.webflux.demo.emojis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Component
class EmojiCounter {

    private static final Logger log = LoggerFactory.getLogger(EmojiCounter.class);

    private final EmojiRepository repository;

    EmojiCounter(EmojiRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void foo() {
        Flux
                .range(1, 10000)
                .flatMap(x -> repository.inc("😂", x))
                .sample(Duration.ofMillis(500))
                .doOnNext(x -> log.info("Increased by {}", x))
                .blockLast();
        final String result = repository.get("😂").block();
        System.out.println(result);
    }

}
