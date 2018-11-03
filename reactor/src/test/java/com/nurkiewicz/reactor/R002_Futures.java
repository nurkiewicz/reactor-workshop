package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.email.EmailSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@Ignore
public class R002_Futures {

	private static final Logger log = LoggerFactory.getLogger(R002_Futures.class);

	private static List<String> EMAILS = List.of("one@example.com", "two@example.com", "three@example.com");
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
		List<String> acks = EMAILS
				.stream()
				.map(e -> executor.submit(() -> EmailSender.sendEmail(e)))
				.collect(toList())
				.stream()
				.map(f -> {
					try {
						return f.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList());

		//then
		assertThat(acks)
				.containsExactly("OK one", "OK two", "OK three");
	}

	/**
	 * TODO Send all e-mails concurrently, but wait for the very first ACK only.
	 */
	@Test(timeout = 500L)
	public void waitForTheFirstEmailOnly() throws Exception {
		//given
		String ack = waitForFirst();

		//then
		await().until(() -> ack, equalTo("OK three"));
	}

	private String waitForFirst() {
		final List<Future<String>> futures = EMAILS
				.stream()
				.map(e -> executor.submit(() -> EmailSender.sendEmail(e)))
				.collect(toList());

		while(true) {
			final Optional<String> first = futures
					.stream()
					.flatMap(f -> {
						try {
							return Optional.ofNullable(f.get(10, MILLISECONDS)).stream();
						} catch(TimeoutException e) {
							return Stream.empty();
						} catch (InterruptedException | ExecutionException e) {
							throw new RuntimeException(e);
						}
					})
					.findFirst();
			if (first.isPresent()) {
				return first.get();
			}
		}
	}

}
