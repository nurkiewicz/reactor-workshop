package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.domains.Crawler;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.function.Function;

@Ignore
public class R073_Expand {

    private static final Logger log = LoggerFactory.getLogger(R073_Expand.class);


    @Test
    public void expand() throws Exception {
        //given
        String word = "Reactor";

        //when
        final Flux<String> expanded = Flux.just(word).expand(this::split);

        //then
        expanded
                .as(StepVerifier::create)
                .expectNext("Reactor")
                .expectNext("Rea")
                .expectNext("ctor")
                .expectNext("R")
                .expectNext("ea")
                .expectNext("ct")
                .expectNext("or")
                .expectNext("e")
                .expectNext("a")
                .expectNext("c")
                .expectNext("t")
                .expectNext("o")
                .expectNext("r")
                .verifyComplete();

        expanded
                .as(StepVerifier::create)
                .expectNext("Reactor")
                .expectNext("Rea", "ctor")
                .expectNext("R", "ea", "ct", "or")
                .expectNext("e", "a", "c", "t", "o", "r")
                .verifyComplete();
    }

    Flux<String> split(String s) {
        if (s.length() <= 1) {
            return Flux.empty();
        }
        return Flux.just(
                s.substring(0, s.length() / 2),
                s.substring(s.length() / 2)
        );
    }

    /**
     * TODO Implement {@link Crawler#outgoingLinks(URI)}
     * <p>
     * This method takes a {@link URI} and returns all URIs found in the contents of a given page.
     * This behaviour is stubbed using a simple map.
     * If there are no outgoing links, just return an empty map.
     * </p>
     */
    @Test
    public void implementCrawling() throws Exception {
        Crawler
                .outgoingLinks(new URI("https://google.com"))
                .as(StepVerifier::create)
                .expectNext(new URI("https://abc.xyz/"))
                .expectNext(new URI("https://gmail.com"))
                .expectNext(new URI("https://maps.google.com"))
                .verifyComplete();

        Crawler
                .outgoingLinks(new URI("https://abc.xyz/"))
                .as(StepVerifier::create)
                .expectNext(new URI("https://abc.xyz/investor/"))
                .verifyComplete();

        Crawler
                .outgoingLinks(new URI("https://abc.xyz/investor/"))
                .as(StepVerifier::create)
                .expectNext(new URI("https://abc.xyz/investor/other/board/"))
                .expectNext(new URI("https://abc.xyz/investor/other/code-of-conduct/"))
                .verifyComplete();

        Crawler
                .outgoingLinks(new URI("https://abc.xyz/investor/other/board/"))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    /**
     * TODO visit all URLs, starting from google.com
     * @see Flux#expand(Function)
     */
    @Test
    public void visitAllUrls() throws Exception {
        //given
        final URI init = new URI("https://google.com");

        //when
        final Flux<URI> allUris = null; // TODO

        //then
        allUris
                .as(StepVerifier::create)
                .expectNext(new URI("https://google.com"))
                .expectNext(new URI("https://abc.xyz/"))
                .expectNext(new URI("https://gmail.com"))
                .expectNext(new URI("https://maps.google.com"))
                .expectNext(new URI("https://abc.xyz/investor/"))
                .expectNext(new URI("https://mail.google.com/mail/u/0"))
                .expectNext(new URI("https://mail.google.com/new"))
                .expectNext(new URI("https://www.google.com/maps/place/Warszawa,+Polska"))
                .expectNext(new URI("https://www.google.com/maps/dir/Warszawa,+Polska"))
                .expectNext(new URI("https://www.google.com/maps/search/Restaurants/"))
                .expectNext(new URI("https://abc.xyz/investor/other/board/"))
                .expectNext(new URI("https://abc.xyz/investor/other/code-of-conduct/"))
                .verifyComplete();

    }

}
