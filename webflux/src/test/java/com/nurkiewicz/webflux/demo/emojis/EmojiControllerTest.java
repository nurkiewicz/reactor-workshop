package com.nurkiewicz.webflux.demo.emojis;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.nurkiewicz.webflux.demo.InitDocker;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class EmojiControllerTest {

	@Autowired
	private EmojiController emojiController;

	@BeforeClass
	public static void init() {
		InitDocker.start().block(Duration.ofMinutes(2));
	}

	@Test
	public void showReturnRawStream() {
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