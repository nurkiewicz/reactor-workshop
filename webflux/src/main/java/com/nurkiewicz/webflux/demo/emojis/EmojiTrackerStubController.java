package com.nurkiewicz.webflux.demo.emojis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class EmojiTrackerStubController {

    private BufferedReader openFile(String file) {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file), UTF_8));
    }

    @GetMapping(value = "/subscribe/eps", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> emojis() {
        return Flux
                .fromStream(() -> openFile("/emojis.txt").lines())
                .repeat()
                .zipWith(Flux.interval(Duration.ofMillis(25)))
                .map(Tuple2::getT1)
                .map(ServerSentEvent::builder)
                .map(ServerSentEvent.Builder::build);
    }

}
