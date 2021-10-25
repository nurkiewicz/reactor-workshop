package com.nurkiewicz.reactor.domains;

import java.net.URL;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class Tld {

	private final String raw;

	public Tld(URL url) {
		this(StringUtils.substringAfterLast(url.getHost(), "."));
	}

	public Tld(String raw) {
		this.raw = raw;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tld)) return false;
		Tld tld = (Tld) o;
		return Objects.equals(raw, tld.raw);
	}

	@Override
	public int hashCode() {
		return Objects.hash(raw);
	}

	@Override
	public String toString() {
		return raw;
	}
}
