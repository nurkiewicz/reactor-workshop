package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.email.Email;
import com.nurkiewicz.reactor.email.Inbox;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;

@Ignore
public class R023_CallbackToFlux {

	private static final Logger log = LoggerFactory.getLogger(R023_CallbackToFlux.class);

	private final Inbox inbox = new Inbox();

	@After
	public void closeInbox() throws Exception {
		inbox.close();
	}

	@Test
	public void oldSchoolCallbackApi() throws Exception {
		inbox.read("test@example.com", email ->
				log.info("You have e-mail\n{}", email)
		);
		TimeUnit.SECONDS.sleep(10);
	}

	/**
	 * TODO Receive first five minutes from any inbox
	 * Hint: <code>emails</code> list must be thread safe
	 * Notice: how do you stop subscription?
	 */
	@Test
	public void getFirstFiveEmails() throws Exception {
		//given
		List<Email> emails = null;

		//when
		inbox.read("foo@example.com", email ->
				log.info("You have e-mail\n{}", email)
		);
		inbox.read("bar@example.com", email ->
				log.info("You have e-mail\n{}", email)
		);

		//then
		await().until(() -> emails, hasSize(5));
	}

	@Test
	public void convertCallbacksToStream() throws Exception {
		//given
		final Flux<Email> emails = Flux.create(sink ->
				inbox.read("foo@example.com", sink::next));

		//when
		final List<Email> list = emails
				.take(5)
				.collectList()
				.block();

		//then
		assertThat(list).hasSize(5);
	}

	@Test
	public void testingUsingStepVerifier() throws Exception {
		//given
		final Flux<Email> emails = Flux.create(sink ->
				inbox.read("foo@example.com", sink::next));

		//when
		emails
				.take(5)
				.as(StepVerifier::create)
				.expectNextCount(5)
				.verifyComplete();
	}

	/**
	 * TODO Merge two streams of messages into a single stream and {@link Flux#take(long)} the first 5.
	 * Hint Static {@link Flux#merge(Publisher[])}
	 */
	@Test
	public void mergeMessagesFromTwoInboxes() throws Exception {
		//given
		final Flux<Email> foo = Flux.create(sink ->
				inbox.read("foo@example.com", sink::next));
		final Flux<Email> bar = Flux.create(sink ->
				inbox.read("bar@example.com", sink::next));

		//when
		Flux<Email> merged = null; //TODO

		//then
		StepVerifier
				.create(merged)
				.expectNextCount(5)
				.verifyComplete();
	}

	@Test
	public void fluxDoesNotAcceptNull() throws Exception {
		Flux
				.create(sink -> inbox.read("spam@example.com", sink::next))
				.blockLast();
	}

	/**
	 * TODO terminate the stream when <code>null</code> is received from callback.
	 * Hint: <code>sink.complete()</code>
	 */
	@Test
	public void handleNullAsEndOfStream() throws Exception {
		//when
		final Flux<Email> emails = Flux
				.create(sink ->
						inbox.read("spam@example.com", e -> {
							if (e != null) {
								sink.next(e);
							} else {
								sink.complete();
							}
						}));

		//then
		emails
				.as(StepVerifier::create)
				.expectNextCount(2)
				.verifyComplete();
	}

	/**
	 * TODO use {@link Flux#as(Function)} operator with {@link #toList(Flux)} method
	 */
	@Test
	public void asOperator() throws Exception {
		//given
		final Flux<Email> foo = Flux.create(sink ->
				inbox.read("foo@example.com", sink::next));

		//when
		final List<Email> emails = toList(foo);

		//then
		assertThat(emails).hasSize(3);
	}

	List<Email> toList(Flux<Email> input) {
		return input
				.take(3)
				.collectList()
				.block();
	}

	@Test
	public void fluxCreateIsNotCached() throws Exception {
		//given
		final Flux<Email> foo = Flux.create(sink -> {
			log.info("Subscribing to e-mails");
			inbox.read("foo@example.com", sink::next);
		});

		//when
		foo.subscribe();
		foo.subscribe();

		//then
	}

}
