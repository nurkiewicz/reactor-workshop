package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.HashMap;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class EmojiController {

    private final URI emojiTrackerUrl;
    private final WebClient webClient;

    public EmojiController(@Value("${emoji-tracker.url}") URI emojiTrackerUrl, WebClient webClient) {
        this.emojiTrackerUrl = emojiTrackerUrl;
        this.webClient = webClient;
    }

    @GetMapping(value = "/emojis/raw", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> raw() {
        return webClient
                .get()
                .uri(emojiTrackerUrl)
                .retrieve()
                .bodyToFlux(ServerSentEvent.class);
    }

    /**
     * TODO How many pushes from /subscribe/eps per second are emitted?
     */
    @GetMapping(value = "/emojis/rps", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Long> rps() {
        return Flux.empty();
    }

    /**
     * TODO How many emojis in total per second are emitted?
     */
    @GetMapping(value = "/emojis/eps", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Long> eps() {
        return Flux.empty();
    }

    /**
     * TODO Total number of each emoji (ever-growing map)
     */
    @GetMapping(value = "/emojis/aggregated", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HashMap<String, Integer>> aggregated() {
        return Flux.empty();
    }

    /**
     * TODO Top 10 most frequent emojis (with count). Only emit when data changes (do not emit subsequent duplicates).
     */
    @GetMapping(value = "/emojis/top10", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HashMap<String, Integer>> top10() {
        return Flux.empty();
    }

    /**
     * TODO Top 10 most frequent emojis (with count), only picture
     * @see #codeToEmoji(String)
     */
    @GetMapping(value = "/emojis/top10str", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<String> top10str() {
        return Flux.empty();
    }

    private static String codeToEmoji(String hex) {
        final String[] codes = hex.split("-");
        if (codes.length == 2) {
            return hexToEmoji(codes[0]) + hexToEmoji(codes[1]);
        } else {
            return hexToEmoji(hex);
        }
    }

    private static String hexToEmoji(String hex) {
        return new String(Character.toChars(Integer.parseInt(hex, 16)));
    }

}
