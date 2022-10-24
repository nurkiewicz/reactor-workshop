package com.nurkiewicz.reactor;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.tools.agent.ReactorDebugAgent;

@Ignore
public class R38_Debugging {

	private static final Logger log = LoggerFactory.getLogger(R38_Debugging.class);

	@BeforeClass
	public static void setup() {
//		Hooks.onOperatorDebug();
		ReactorDebugAgent.init();
		ReactorDebugAgent.processExistingClasses();

		BlockHound.install();
	}

	@Test
	public void showStackTrace() throws InterruptedException {
		Mono.zip(
						Mono.never().timeout(Duration.ofMillis(10)),
						Mono.never().timeout(Duration.ofMillis(100))
				)
				.timeout(Duration.ofMillis(20))
				.map(x -> x)
				.checkpoint("Handling GET")
				.filter(x -> true)
				.timeout(Duration.ofMillis(70))
				.subscribe();
		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void test_38() {

		Mono.fromCallable(() -> {
					try (InputStream conn = new URL("http://www.example.com").openStream()) {
						return null;
					}
				})
				.subscribeOn(Schedulers.parallel())  //uncomment this
//				.subscribeOn(Schedulers.boundedElastic())
				.block();
	}

	@Test
	public void test_57() {
		Mono
				.delay(Duration.ofSeconds(1))
				.publishOn(Schedulers.boundedElastic())
				.doOnNext(it -> {
					try {
						try (InputStream conn = new URL("http://www.example.com").openStream()) {

						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				})
				.block();

	}

}
