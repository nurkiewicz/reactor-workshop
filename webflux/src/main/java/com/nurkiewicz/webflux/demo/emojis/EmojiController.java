package com.nurkiewicz.webflux.demo.emojis;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    Flux<Long> eps() {
        return retrieve()
                .bodyToFlux(Map.class)
                .window(Duration.ofSeconds(1))
                .flatMap(win -> win.reduce(0L, (c, data) -> c + sumValues(data)));
    }

    private long sumValues(Map data) {
        return data
                .values()
                .stream()
                .mapToInt(x -> (Integer) x)
                .sum();
    }

    /**
     * TODO Total number of each emoji (ever-growing map)
     */
    @GetMapping(value = "/emojis/aggregated", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HashMap<String, Integer>> aggregated() {
        return retrieve()
                .bodyToFlux(Map.class)
                .scan(new HashMap<>(), (HashMap<String, Integer> acc, Map data) -> {
                    final HashMap<String, Integer> result = new HashMap<>(acc);
                    data.forEach((k, v) -> {
                        result.merge(k.toString(), (int) v, (old, n) -> old + n);
                    });
                    return result;
                });
    }

    /**
     * TODO Top 10 most frequent emojis (with count)
     */
    @GetMapping(value = "/emojis/top10", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HashMap<String, Integer>> top10() {
        return aggregated()
                .map(this::top10values);
    }

    private HashMap<String, Integer> top10values(Map<String, Integer> agg) {
        return new HashMap<>(agg
                .entrySet()
                .stream()
                .sorted(comparing(e -> -e.getValue()))
                .limit(10)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    /**
     * TODO Top 10 most frequent emojis (with count), only picture
     * @see #codeToEmoji(String)
     */
    @GetMapping(value = "/emojis/top10str", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<String> top10str() {
        return top10()
                .map(this::keysAsOneString);
    }

    private String keysAsOneString(HashMap<String, Integer> m) {
        return m
                .keySet()
                .stream()
                .map(EmojiController::codeToEmoji)
                .collect(Collectors.joining());
    }

    @NotNull
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
