package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class EmojiTrackerStubController {

    private final Flux<String> stream;

    public EmojiTrackerStubController() {
        final ConnectableFlux<String> flux = Flux
                .fromStream(() -> openFile("/emojis.txt").lines())
                .repeat()
                .zipWith(Flux.interval(Duration.ofMillis(25)))
                .map(Tuple2::getT1)
                .publish();
        flux.connect();
        stream = flux;
    }

    private BufferedReader openFile(String file) {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file), UTF_8));
    }

    @GetMapping(value = "/subscribe/eps", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> emojis() {
        return stream
                .map(ServerSentEvent::builder)
                .map(ServerSentEvent.Builder::build);
    }

}
