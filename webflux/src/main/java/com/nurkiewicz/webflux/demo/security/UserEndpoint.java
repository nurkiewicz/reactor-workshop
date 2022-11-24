package com.nurkiewicz.webflux.demo.security;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import reactor.core.publisher.Mono;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserEndpoint {

	private final DatabaseClient databaseClient;

	public UserEndpoint(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@GetMapping("/users")
	Mono<List<User>> users() {
		return databaseClient
				.sql("SELECT * FROM users")
				.fetch()
				.all()
				.map(User::new)
				.collectList();
	}

	@PostMapping("/users")
	Mono<User> create(@RequestBody User user) {
		return databaseClient
				.sql("INSERT INTO users (username, role) VALUES (:username, :role)")
				.bind("username", user.getName())
				.bind("role", user.getRole())
				.fetch()
				.rowsUpdated()
				.then(Mono.just(user));
	}

}

class User {
	private final String name;
	private final String role;

	@JsonCreator
	User(String name, String role) {
		this.name = name;
		this.role = role;
	}

	User(Map<String, Object> record) {
		this(
				Objects.toString(record.get("username"), null),
				Objects.toString(record.get("role"), null));
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}
}
