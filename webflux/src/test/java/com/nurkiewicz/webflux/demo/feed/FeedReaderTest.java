package com.nurkiewicz.webflux.demo.feed;

import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurkiewicz.webflux.demo.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class FeedReaderTest extends IntegrationTest {

    @Test
    public void testGetAsync() throws MalformedURLException {
        //given
        Mono<String> htmlMono = new FeedReader().getAsync(new URL("http://www.example.com"));

        //when
        String html = htmlMono.block();

        //then
        assertThat(html).contains("Example Domain");
    }

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

