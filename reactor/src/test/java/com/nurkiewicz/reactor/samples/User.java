package com.nurkiewicz.reactor.samples;

public class User {

	private final long id;

	public User(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "User{id=" + id + '}';
	}
}
