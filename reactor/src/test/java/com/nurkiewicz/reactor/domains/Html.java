package com.nurkiewicz.reactor.domains;

import java.util.Objects;

public class Html {

	private final String raw;

	public Html(String raw) {
		this.raw = raw;
	}

	public String getRaw() {
		return raw;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Html html = (Html) o;
		return Objects.equals(raw, html.raw);
	}

	@Override
	public int hashCode() {
		return Objects.hash(raw);
	}

	@Override
	public String toString() {
		return "Html{'" + raw + "'}";
	}
}
