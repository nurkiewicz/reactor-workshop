package com.nurkiewicz.reactor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R051_WhatIsAscheduler {

	private static final Logger log = LoggerFactory.getLogger(R051_WhatIsAscheduler.class);

	/**
	 * TODO Implement {@link #customScheduler()}
	 */
	@Test
	public void createCustomScheduler() throws Exception {
		//given
		AtomicReference<String> seenThread = new AtomicReference<>();
		final Mono<Void> mono = Mono.fromRunnable(() -> {
			seenThread.set(Thread.currentThread().getName());
		});

		//when
		mono
				.subscribeOn(customScheduler())
				.block();

		//then
		assertThat(seenThread.get()).matches("Custom-\\d+");
	}

	/**
	 * TODO Implement custom bound scheduler.
	 * It must contain 10 threads named "Custom-" and a sequence number.
	 * @see ExecutorService
	 * @see ThreadFactoryBuilder
	 * @see Schedulers#fromExecutorService(ExecutorService)
	 */
	private Scheduler customScheduler() {
		return Schedulers.elastic();
	}

}
