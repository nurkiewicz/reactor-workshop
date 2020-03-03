package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
class EmojiRepository {

    Mono<Long> inc(String emoji, long by) {
        return Mono.empty();
    }

    Mono<String> get(String emoji) {
        return Mono.empty();
    }

}
