package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableRedisRepositories
class RedisConfiguration {

    @Bean
    public ReactiveRedisConnectionFactory connectionFactory(@Value("${spring.redis.port}") int port) {
        return new LettuceConnectionFactory("localhost", port);
    }

    @Bean
    ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    RedisSerializationContext<String, String> serializationContext() {
        return RedisSerializationContext.fromSerializer(RedisSerializer.string());
    }

}
