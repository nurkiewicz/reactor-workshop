package com.nurkiewicz.reactor.user;

import com.devskiller.jfairy.Fairy;

public class LoremIpsum {

	private static final Fairy fairy = Fairy.create();

	public static String[] words() {
		return text().split("\\s");
	}

	public static String text() {
		return fairy.textProducer().loremIpsum();
	}

}
