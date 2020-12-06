package com.nurkiewicz.webflux.demo.security;

import java.lang.invoke.MethodHandles;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecretService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final Scheduler scheduler = Schedulers.newBoundedElastic(10, 100, "Secrets");

	public Flux<Secret> userSecrets() {
		return generateSecrets(10, "alpha");
	}

	@PreAuthorize("hasRole('ADMIN')")
	public Flux<Secret> adminsSecrets() {
		return generateSecrets(3, "beta");
	}

	private Flux<Secret> generateSecrets(int count, String key) {
		log.debug("About to generate {} secrets for {}", count, key);
		return Flux
				.range(1, count)
				.flatMap(i -> genSecret(key, i));
	}

	private Mono<Secret> genSecret(String key, Integer i) {
		return Mono.fromCallable(() -> key + "-" + i)
				.subscribeOn(scheduler)
				.delayElement(randomDuration())
				.flatMap(k -> currentUserName().map(user -> new Secret(key + "-" + i, user + "'s secret value #" + i)))
				.doOnNext(s -> log.info("Generated {}", s));
	}

	private Duration randomDuration() {
		return Duration.ofMillis((long) (Math.random() * 100));
	}

	private Mono<String> currentUserName() {
		return ReactiveSecurityContextHolder
				.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getPrincipal)
				.cast(User.class)
				.map(User::getUsername);
	}

}
