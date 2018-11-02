package com.nurkiewicz.reactor.email;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import com.devskiller.jfairy.producer.text.TextProducer;

public class Email {

	private final String from;
	private final String to;
	private final String subject;
	private final String body;

	public static Email random(Fairy fairy) {
		return Email.random(fairy.person().getEmail(), fairy);
	}

	public static Email random(String to, Fairy fairy) {
		final Person from = fairy.person();
		final TextProducer text = fairy.textProducer();
		return new Email(from.getEmail(), to, text.sentence(), text.paragraph());
	}

	public Email(String from, String to, String subject, String body) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "FROM:\t\t" + from + "\nTO:\t\t\t" + to + "\nSUBJECT:\t" + subject + "\n" + body + '\n';
	}
}
