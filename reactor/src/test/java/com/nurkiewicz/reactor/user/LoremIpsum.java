package com.nurkiewicz.reactor.user;

import com.devskiller.jfairy.Fairy;
import reactor.core.publisher.Flux;

public class LoremIpsum {

	private static final Fairy fairy = Fairy.create();

	public static Flux<String> wordStream() {
		return Flux
				.just(words())
				.map(word -> word.replaceAll("[.,]", ""));
	}

	public static String[] words() {
		return text().split("\\s");
	}

	public static String text() {
		return fairy.textProducer().loremIpsum();
	}

}
