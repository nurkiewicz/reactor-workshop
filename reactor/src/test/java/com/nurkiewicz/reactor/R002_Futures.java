package com.nurkiewicz.reactor;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@Ignore
public class R002_Futures {

	private static final Logger log = LoggerFactory.getLogger(R002_Futures.class);

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
	 * TODO Use ExecutorService to create a Future
	 */
	@Test
	public void shouldProcessOneEmailInExecutorService() throws Exception {
		//given

		//when
		Future<String> future = null; //...

		//then
		assertThat(future.get()).isEqualTo("OK one");
	}

	/**
	 * TODO Submit many tasks to ExecutorService, all at once
	 */
	@Test
	public void shouldProcessAllEmailsConcurrentlyInExecutorService() throws Exception {
		//given

		//when
		List<String> acks = null; //

		//then
		assertThat(acks)
				.containsExactly("OK one", "OK two", "OK three");
	}

	/**
	 * TODO Send all e-mails concurrently, but wait for the very first ACK only.
	 */
	@Test
	public void waitForTheFirstEmailOnly() throws Exception {
		//given
		String ack = null;

		//when
		EMAILS.stream(); //continue from here

		//then
		await().until(() -> ack, equalTo("OK three"));
	}

}
