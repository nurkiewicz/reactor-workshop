package com.nurkiewicz.webflux.demo.emojis;

import java.net.URI;
import java.util.Map;

import org.junit.Test;
import reactor.test.StepVerifier;
import reactor.util.Loggers;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import static java.time.Duration.ofSeconds;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class EmojiControllerTest {

	public static final URI EMOJI_TRACKER_URL = URI.create("http://example.com");

	private EmojiController emojiController() {
		ResponseSpec rs = mock(ResponseSpec.class);
		given(rs.bodyToFlux(ServerSentEvent.class)).willReturn(new EmojiTrackerStubController().stubSseStream());
		given(rs.bodyToFlux(any(ParameterizedTypeReference.class))).willReturn(new EmojiTrackerStubController().stubMapStream());
		RequestHeadersSpec rhs = mock(RequestHeadersSpec.class);
		given(rhs.retrieve()).willReturn(rs);
		RequestHeadersUriSpec rhus = mock(RequestHeadersUriSpec.class);
		given(rhus.uri(EMOJI_TRACKER_URL)).willReturn(rhs);
		WebClient webClientStub = mock(WebClient.class);
		given(webClientStub.get()).willReturn(rhus).getMock();
		return new EmojiController(EMOJI_TRACKER_URL, webClientStub);
	}

	@Test
	public void shouldReturnRawStream() {
		StepVerifier.withVirtualTime(() -> emojiController()
				.raw()
				.map(sse -> (Map<String, Integer>) sse.data())
				.take(5))
				.expectSubscription()
				.thenAwait(ofSeconds(1))
				.expectNext(Map.of("1F606", 1, "1F60E", 1))
				.expectNext(Map.of("1F60A", 1))
				.expectNext(Map.of("1F60A", 4, "1F60E", 2))
				.expectNext(Map.of("1F495", 4, "1F606", 2, "1F61E", 1))
				.expectNext(Map.of("1F495", 1, "1F600", 1, "1F614", 1, "2764", 1))
				.verifyComplete();
	}

	/**
	 * TODO How many pushes from /subscribe/eps per second are emitted?
	 */
	@Test(timeout = 5000)
	public void shouldReturnUpdatesPerSecond() {
		StepVerifier.withVirtualTime(() -> emojiController()
				.rps()
				.take(4)
		)
				.expectSubscription()
				.thenAwait(ofSeconds(1))
				.expectNext(5L)
				.thenAwait(ofSeconds(1))
				.expectNext(5L)
				.thenAwait(ofSeconds(1))
				.expectNext(5L)
				.thenAwait(ofSeconds(1))
				.expectNext(5L)
				.verifyComplete();
	}

	/**
	 * TODO How many emojis in total per second are emitted?
	 *
	 * Hint: use:
	 *
	 * <pre>
	 *     .bodyToFlux(new ParameterizedTypeReference<Map<String, Integer>>() {})
	 * </pre>
	 */
	@Test(timeout = 5000)
	public void shouldReturnEmojisPerSecond() {
		StepVerifier.withVirtualTime(() -> emojiController()
				.eps()
				.take(4)
		)
				.expectSubscription()
				.thenAwait(ofSeconds(1))
				.expectNext(20)
				.thenAwait(ofSeconds(1))
				.expectNext(13)
				.thenAwait(ofSeconds(1))
				.expectNext(10)
				.thenAwait(ofSeconds(1))
				.expectNext(17)
				.verifyComplete();
	}

	/**
	 * TODO Total number of each emoji (ever-growing map)
	 *
	 * Example input:
	 * <code>
	 *   data:{"2600":1,"2728":1}
	 *   data:{"1F602":1,"2600":2,"2764":1}
	 *   data:{"2728":4,"2828":1}
	 * </code>
	 *
	 * Example output:
	 * <code>
	 *   data:{"2600":1,"2728":1}
	 *   data:{"2600":3,"2728":1,"1F602":1,"2764":1}
	 *   data:{"2600":3,"2728":5,"1F602":1,"2764":1,"2828":1}
	 * </code>
	 */
	@Test(timeout = 5000)
	public void shouldReturnAggregatedEmojis() {
		StepVerifier.withVirtualTime(() -> emojiController()
				.aggregated()
				.log(Loggers.getLogger(EmojiControllerTest.class))
				.take(7)
		)
				.expectSubscription()
				.thenAwait(ofSeconds(1))
				.expectNext(Map.of())
				.expectNext(Map.of("1F606", 1))
				.expectNext(Map.of("1F606", 1, "1F60E", 1))
				.expectNext(Map.of("1F606", 1, "1F60A", 1, "1F60E", 1))
				.expectNext(Map.of("1F606", 1, "1F60A", 1, "1F60E", 3))
				.expectNext(Map.of("1F606", 1, "1F60A", 1 + 4, "1F60E", 3))
				.expectNext(Map.of("1F606", 1 + 2, "1F60A", 5, "1F60E", 3))
				.verifyComplete();
	}

	/**
	 * TODO Top most frequent emojis (with count). Only emit when data changes (do not emit subsequent duplicates).
	 */
	@Test(timeout = 5000)
	public void shouldReturnTop3() {
		StepVerifier.withVirtualTime(() -> emojiController()
				.top(4)
				.log(Loggers.getLogger(EmojiControllerTest.class))
				.take(10)
		)
				.expectSubscription()
				.thenAwait(ofSeconds(5))
				.expectNext(Map.of())
				.expectNext(Map.of("1F606", 1))
				.expectNext(Map.of("1F606", 1, "1F60E", 1))
				.expectNext(Map.of("1F606", 1, "1F60A", 1, "1F60E", 1))
				.expectNext(Map.of("1F606", 1, "1F60A", 1, "1F60E", 1 + 2))
				.expectNext(Map.of("1F606", 1, "1F60A", 1 + 4, "1F60E", 3))
				.expectNext(Map.of("1F606", 1 + 2, "1F60A", 5, "1F60E", 3))
				.expectNext(Map.of("1F606", 3, "1F60A", 5, "1F60E", 3, "1F495", 4))
				.expectNext(Map.of("1F606", 3, "1F60A", 5, "1F60E", 3, "1F495", 4 + 1))
				.expectNext(Map.of("1F606", 3, "1F60A", 5, "1F60E", 3 + 1, "1F495", 5))
				.verifyComplete();
	}

	@Test(timeout = 5000)
	public void shouldReturnTopStr() {
		StepVerifier.withVirtualTime(() -> emojiController()
				.topStr(4)
				.log(Loggers.getLogger(EmojiControllerTest.class))
				.take(6)
		)
				.expectSubscription()
				.thenAwait(ofSeconds(5))
				.expectNext("", "😆", "😆😎", "😊😆😎", "💕😊😆😎", "😂💕😊😎")
				.verifyComplete();
	}

}