package com.nurkiewicz.webflux.demo.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Ignore
public class FeedReaderTest {

    @Test
    public void test_8() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        WebClient
                .create("https://raw.githubusercontent.com/jvm-bloggers/jvm-bloggers/master/src/main/resources/blogs/bloggers.json")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(s -> Mono.fromCallable(() -> objectMapper.readValue(s, Response.class)))
                .flatMapIterable(Response::getBloggers)
                .map(Blog::toString)
                .doOnNext(System.out::println)
                .blockLast();
    }

}

