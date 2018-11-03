package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.email.EmailSender;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static com.nurkiewicz.reactor.email.EmailSender.sendEmail;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@Ignore
public class R001_BlockingCode {

	private static final Logger log = LoggerFactory.getLogger(R001_BlockingCode.class);

	public static List<String> EMAILS = List.of("one@example.com", "two@example.com", "three@example.com");
	public static String ONE_EMAIL = EMAILS.get(0);

	private ExecutorService executor;

	/**
	 * TODO Add 500 millisecond timeout to this test
	 */
	@Test
	public void shouldRunSequentially() throws Exception {
		//given

		//when
		List<String> acks = EMAILS
				.stream()
				.map(EmailSender::sendEmail)
				.collect(toList());

		//then
		assertThat(acks)
				.containsExactly("OK one", "OK two", "OK three");
	}

	@Test
	public void shouldProcessOneEmailInBackgroundUsingNewThread() throws Exception {
		//given
		AtomicReference<String> ack = new AtomicReference<>();
		Runnable block = () -> ack.set(sendEmail(ONE_EMAIL));

		//when
		new Thread(block, "EmailSender").start();

		//then
		await().untilAtomic(ack, equalTo("OK one"));
	}

	/**
	 * TODO Create many threads and process e-mails concurrently
	 * Is ArrayList OK?
	 * What about order?
	 */
	@Test
	public void shouldProcessMultipleEmailsInBackground() throws Exception {
		//given
		final List<String> acks = new CopyOnWriteArrayList<>();

		//when


		//then
		await().until(() -> acks, equalTo(List.of("OK one", "OK two", "OK three")));
	}


}
