package com.nurkiewicz.reactor.samples;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RestClient {

	private final Map<Integer, Boolean> seen = new ConcurrentHashMap<>();

	public void post(int id) {
		if (seen.putIfAbsent(id, true) != null) {
			throw new IllegalArgumentException("Duplicated call for " + id);
		}
	}

}
