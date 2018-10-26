package com.nurkiewicz.reactor;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Ignore
public class R003_CompletableFutures {

	private static final Logger log = LoggerFactory.getLogger(R003_CompletableFutures.class);

	private static List<String> EMAILS = Arrays.asList("one@example.com", "two@example.com", "three@example.com");
	private static String ONE_EMAIL = EMAILS.get(0);

	private ExecutorService executor;

	@Before
	public void startExecutorService() {
		executor = Executors.newFixedThreadPool(10);
	}

	@After
	public void closeExecutorService() {
		executor.shutdownNow();
	}

	/**
	 * TODO Create CompletableFuture for each e-mail, collect them to list asynchronously
	 * Hint {@link CompletableFuture#allOf(CompletableFuture[])}
	 */
	@Test
	public void waitForAllEmailsToCompleteUsingCompletableFuture() throws Exception {
		//given

		//when

		//then
	}

	/**
	 * TODO CompletableFuture for each e-mail, asynchronously receive the very first one
	 * Hint: {@link CompletableFuture#anyOf(CompletableFuture[])}
	 */
	@Test
	public void waitForFirstEmailToCompleteUsingCompletableFuture() throws Exception {
		//given

		//when

		//then
	}

}
