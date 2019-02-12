package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Opml;
import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Component
public class OpmlReader {

    public Flux<Outline> allFeeds() {
        return Flux.fromStream(() -> {
            WireFeedInput input = new WireFeedInput();
            try (final InputStream inputStream = OpmlReader.class.getResourceAsStream("/feed-jvm-bloggers.xml")) {
                final InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                final Reader reader = new BufferedReader(streamReader);
                Opml feed = (Opml) input.build(reader);
                return feed.getOutlines().stream();
            } catch (IOException | FeedException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
