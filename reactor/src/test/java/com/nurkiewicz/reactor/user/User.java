package com.nurkiewicz.reactor.user;

import java.util.Objects;

public class User {

	private final long id;

	public User(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return id == user.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "User{id=" + id + '}';
	}
}
