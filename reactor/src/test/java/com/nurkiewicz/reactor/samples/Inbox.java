package com.nurkiewicz.reactor.samples;

import com.devskiller.jfairy.Fairy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Inbox implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(Inbox.class);

	private static final Fairy fairy = Fairy.create();

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

	public void read(String email, Consumer<Email> onEmail) {
		switch (email) {
			case "spam@example.com":
				scheduleEmailInTheFuture(email, 2, onEmail);
				return;
			default:
				scheduleEmailInTheFuture(email, Long.MAX_VALUE, onEmail);
		}
	}

	private void scheduleEmailInTheFuture(String email, long total, Consumer<Email> onEmail) {
		if (total > 0) {
			Runnable run = pushEmailAndContinue(email, total, onEmail);
			final long randomSleep = (long) (500 + ThreadLocalRandom.current().nextGaussian() * 200);
			scheduler.schedule(run, randomSleep, TimeUnit.MILLISECONDS);
		} else {
			try {
				onEmail.accept(null);
			} catch (Exception e) {
				log.error("Error pushing null");
			}
		}
	}

	private Runnable pushEmailAndContinue(String email, long total, Consumer<Email> onEmail) {
		return () -> {
			pushEmail(email, onEmail);
			scheduleEmailInTheFuture(email, total - 1, onEmail);
		};
	}

	private void pushEmail(String email, Consumer<Email> onEmail) {
		try {
			onEmail.accept(Email.random(email, fairy));
		} catch (Exception e) {
			log.error("Error pushing e-mail", e);
		}
	}

	@Override
	public void close() throws Exception {
		scheduler.shutdownNow();
	}
}

