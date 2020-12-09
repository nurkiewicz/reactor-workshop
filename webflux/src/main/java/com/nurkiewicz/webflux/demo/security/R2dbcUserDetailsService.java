package com.nurkiewicz.webflux.demo.security;

import java.util.List;

import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
class R2dbcUserDetailsService implements ReactiveUserDetailsService {

	private final DatabaseClient databaseClient;

	R2dbcUserDetailsService(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return databaseClient
				.execute("SELECT password, role FROM users where username = :user")
				.bind("user", username)
				.fetch()
				.one()
				.map(result -> new User(username, result.get("password").toString(), List.of(new SimpleGrantedAuthority(result.get("role").toString()))));


	}
}
