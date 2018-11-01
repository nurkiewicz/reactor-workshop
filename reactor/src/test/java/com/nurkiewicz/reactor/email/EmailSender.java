package com.nurkiewicz.reactor.email;

import com.nurkiewicz.reactor.samples.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.Duration.ofMillis;

public class EmailSender {

	private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

	public static String sendEmail(String address) {
		log.info("About to send e-mail to {}", address);
		final int randMillis = address.contains("three")? 20 : 400;
		Sleeper.sleepRandomly(ofMillis(randMillis));
		return "OK " + address.substring(0, address.indexOf('@'));
	}

}
