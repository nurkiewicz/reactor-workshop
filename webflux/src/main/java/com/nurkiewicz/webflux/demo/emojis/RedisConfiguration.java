package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableRedisRepositories
class RedisConfiguration {

    @Bean
    RedisSerializationContext<String, String> serializationContext() {
        return RedisSerializationContext.fromSerializer(RedisSerializer.string());
    }

}
