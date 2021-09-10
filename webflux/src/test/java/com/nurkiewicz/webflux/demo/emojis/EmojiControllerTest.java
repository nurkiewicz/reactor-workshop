package com.nurkiewicz.webflux.demo.emojis;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class EmojiControllerTest {

	public static final URI EMOJI_TRACKER_URL = URI.create("http://example.com");

	private EmojiController emojiController = new EmojiController(EMOJI_TRACKER_URL, webClientStub());

	private WebClient webClientStub() {
		ResponseSpec rs = mock(ResponseSpec.class);
		given(rs.bodyToFlux(ServerSentEvent.class)).willReturn(new EmojiTrackerStubController().emojis());
		RequestHeadersSpec rhs = mock(RequestHeadersSpec.class);
		given(rhs.retrieve()).willReturn(rs);
		RequestHeadersUriSpec rhus = mock(RequestHeadersUriSpec.class);
		given(rhus.uri(EMOJI_TRACKER_URL)).willReturn(rhs);
		WebClient wtc = mock(WebClient.class);
		given(wtc.get()).willReturn(rhus).getMock();
		return wtc;
	}

	@Test
	public void shouldReturnRawStream() {
		final List<Map<String, Integer>> events = emojiController
				.raw()
				.map(sse -> (Map<String, Integer>) sse.data())
				.take(10)
				.collectList()
				.block();

		assertThat(events)
				.containsExactly(
						Map.of("1F606", 1,"1F60E", 1),
						Map.of("1F60A", 1),
						Map.of("1F60C", 1),
						Map.of("0031-20E3", 1,"1F49F", 1,"1F601", 1,"1F61E", 1,"1F62D", 1),
						Map.of("1F495", 1,"1F600", 1,"1F614", 1,"2764", 1),
						Map.of("1F602", 1),
						Map.of("1F49C", 1,"1F60E", 1,"1F620", 1),
						Map.of("1F602", 2,"1F605", 1,"2764", 1),
						Map.of("1F37A", 1,"1F634", 1,"2764", 1),
						Map.of("1F614", 1,"1F622", 1)
						);
	}

}