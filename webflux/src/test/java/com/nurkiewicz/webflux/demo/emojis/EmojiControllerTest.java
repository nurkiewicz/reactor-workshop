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
		System.out.println("events = " + events);
	}

}