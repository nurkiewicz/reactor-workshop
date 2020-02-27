package com.nurkiewicz.webflux.demo.feed;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.net.URI;

public interface ArticleRepository extends ReactiveMongoRepository<Article, URI> {
}
