package com.nurkiewicz.webflux.demo.emojis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class EmojiTrackerStubController {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private BufferedReader openFile(String file) {
		return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file), UTF_8));
	}

	@GetMapping(value = "/subscribe/eps", produces = TEXT_EVENT_STREAM_VALUE)
	Flux<ServerSentEvent> emojis() {
		return stubSseStream();
	}

	Flux<ServerSentEvent> stubSseStream() {
		return stubMapStream()
				.map(ServerSentEvent::builder)
				.map(ServerSentEvent.Builder::build);
	}

	Flux<Map> stubMapStream() {
		return Flux
				.fromStream(() -> openFile("/emojis.txt").lines())
				.map(this::toJson)
				.repeat()
				.buffer(5)
				.zipWith(Flux.interval(Duration.ofMillis(500), Duration.ofSeconds(1)))
				.concatMapIterable(Tuple2::getT1);
	}

	private Map toJson(String s) {
		try {
			return objectMapper.readValue(s, Map.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
