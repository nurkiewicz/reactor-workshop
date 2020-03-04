package com.nurkiewicz.webflux.demo.feed;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FeedReader {

    private final WebClient webClient;

    public FeedReader(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<SyndEntry> fetch(String url) {
        return getAsync(url)
                .flatMapIterable(this::parseFeed);
    }

    private Iterable<SyndEntry> parseFeed(String feedBody) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream is = new ByteArrayInputStream(applyAtomNamespaceFix(feedBody).getBytes(UTF_8));
            Document doc = builder.parse(is);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(doc);
            return feed.getEntries();
        } catch (ParserConfigurationException | SAXException | IOException | FeedException e) {
            throw new RuntimeException(e);
        }
    }

    private String applyAtomNamespaceFix(String feedBody) {
        return feedBody.replace("https://www.w3.org/2005/Atom", "http://www.w3.org/2005/Atom");
    }

    private Mono<String> getAsync(String url) {
        return webClient
                .get()
                .uri(url)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode().is3xxRedirection()) {
                        return followRedirect(response);
                    } else {
                        return response.bodyToMono(String.class);
                    }
                });
    }

    @NotNull
    private Mono<? extends String> followRedirect(ClientResponse response) {
        String redirectUrl = response.headers().header("Location").get(0);
        return response.bodyToMono(Void.class).then(getAsync(redirectUrl));
    }

}
