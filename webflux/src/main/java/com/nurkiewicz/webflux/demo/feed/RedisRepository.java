package com.nurkiewicz.webflux.demo.feed;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisRepository {

    private final ReactiveStringRedisTemplate redis;

    public RedisRepository(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

}
