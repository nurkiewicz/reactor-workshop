package com.nurkiewicz.reactor.samples;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Sleeper {

	public static void sleepRandomly(Duration duration) {
		long millis = duration.toMillis();
		double randMillis = millis + ThreadLocalRandom.current().nextGaussian() * millis / 10;
		sleep(Duration.ofMillis((long) randMillis));
	}

	public static void sleep(Duration duration) {
		try {
			TimeUnit.MILLISECONDS.sleep(duration.toMillis());
		} catch (InterruptedException ignored) {
		}
	}

}
