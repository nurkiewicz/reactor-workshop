package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
class EmojiRepository {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public EmojiRepository(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    Mono<Long> inc(String emoji, long by) {
        return Mono.empty();
    }

    Mono<String> get(String emoji) {
        return Mono.empty();
    }

}
