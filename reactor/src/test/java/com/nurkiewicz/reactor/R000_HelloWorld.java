package com.nurkiewicz.reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class R000_HelloWorld {

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

}
