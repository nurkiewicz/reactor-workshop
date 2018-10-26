package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R000_HelloWorld {

	private MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

	public R000_HelloWorld() throws NoSuchAlgorithmException { }

	@Test
	public void hello() {
		//given

		//when
		Mono<Integer> mono = Mono.just(1);

		//then
		mono
				.as(StepVerifier::create)
				.expectNext(1)
				.expectComplete()
				.verify();
	}

	/**
	 * The password is the name of this test
	 */
	@Test
	public void pleaseReadTheJavaDoc() throws Exception {
		//given
		var pwd = "what's the password?";

		//when
		final byte[] hash = sha256.digest(pwd.getBytes(UTF_8));

		//then
		assertThat(hash)
				.containsExactly(24, -106, 92, 124, -16, 116, -59, -106, 99, -105, 81, -111, 89, -114, -37, -125, 88, -108, -96, -76, 66, 90, -44, -15, 35, 30, -115, -90, -60, -89, 56, -116);
	}

}
