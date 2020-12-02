package com.nurkiewicz.webflux.demo.emojis;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
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
        return retrieve()
            .bodyToFlux(ServerSentEvent.class);
    }

    /**
     * TODO How many pushes from /subscribe/eps per second are emitted?
     */
    @GetMapping(value = "/emojis/rps", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Long> rps() {
        return retrieve()
            .bodyToFlux(ServerSentEvent.class)
            .window(Duration.ofSeconds(1))
            .flatMap(Flux::count);
    }

    /**
     * TODO How many emojis in total per second are emitted?
     */
    @GetMapping(value = "/emojis/eps", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Integer> eps() {
        return retrieve()
            .bodyToFlux(new ParameterizedTypeReference<Map<String, Integer>>() {})
            .concatMapIterable(Map::values)
            .window(Duration.ofSeconds(1))
            .flatMap(win -> win.reduce(Integer::sum));
    }

    /**
     * TODO Total number of each emoji (ever-growing map)
     */
    @GetMapping(value = "/emojis/aggregated", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Map<String, Integer>> aggregated() {
        return retrieve()
            .bodyToFlux(new ParameterizedTypeReference<Map<String, Integer>>() {})
            .concatMapIterable(Map::entrySet)
            .scan(new HashMap<>(), ( acc, entry) -> {
                final HashMap<String, Integer> result = new HashMap<>(acc);
                result.merge(entry.getKey(), entry.getValue(), Integer::sum);
                return result;
            });
    }

    /**
     * TODO Top 10 most frequent emojis (with count). Only emit when data changes (do not emit subsequent duplicates).
     */
    @GetMapping(value = "/emojis/top10", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Map<String, Integer>> top10() {
        return aggregated()
            .map(this::top10values)
            .distinctUntilChanged();
    }

    private Map<String, Integer> top10values(Map<String, Integer> agg) {
        return new HashMap<>(agg
            .entrySet()
            .stream()
            .sorted(comparing(e -> -e.getValue()))
            .limit(10)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    /**
     * TODO Top 10 most frequent emojis (without count), only picture
     * @see #codeToEmoji(String)
     */
    @GetMapping(value = "/emojis/top10str", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<String> top10str() {
        return top10()
            .map(this::keysAsOneString);
    }

    private String keysAsOneString(Map<String, Integer> m) {
        return m
            .keySet()
            .stream()
            .map(EmojiController::codeToEmoji)
            .collect(Collectors.joining());
    }

    private WebClient.ResponseSpec retrieve() {
        return webClient
            .get()
            .uri(emojiTrackerUrl)
            .retrieve();
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