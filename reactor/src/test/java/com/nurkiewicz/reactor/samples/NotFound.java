package com.nurkiewicz.reactor.samples;

public class NotFound extends RuntimeException {
	public NotFound(String message) {
		super(message);
	}
}