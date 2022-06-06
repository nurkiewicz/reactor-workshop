package com.nurkiewicz.webflux.demo.security;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class R2dbcUserDetailsService implements ReactiveUserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DatabaseClient databaseClient;

	R2dbcUserDetailsService(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	@Transactional
	public Mono<UserDetails> findByUsername(String username) {
		return databaseClient
				.sql("SELECT password, role FROM users where username = :user")
				.bind("user", username)
				.fetch()
				.one()
				.map(result -> (UserDetails)new User(username, result.get("password").toString(), List.of(new SimpleGrantedAuthority(result.get("role").toString()))))
				.doOnSubscribe(s -> log.debug("Loading user for {}", username))
				.doOnNext(u -> log.debug("Got user. Name={}, role={}", u.getUsername(), u.getAuthorities()));
	}
}
