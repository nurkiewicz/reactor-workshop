package com.nurkiewicz.reactor.domains;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Domain {

	private final URL url;
	private final long linkingRootDomains;
	private final long externalLinks;
	private final float mozRank;
	private final float mozTrust;

	Domain(URL url, long linkingRootDomains, long externalLinks, float mozRank, float mozTrust) {
		this.url = url;
		this.linkingRootDomains = linkingRootDomains;
		this.externalLinks = externalLinks;
		this.mozRank = mozRank;
		this.mozTrust = mozTrust;
	}

	public URL getUrl() {
		return url;
	}

	public URI getUri() {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getTld() {
		return StringUtils.substringAfterLast(url.getHost(), ".");
	}

	public long getLinkingRootDomains() {
		return linkingRootDomains;
	}

	public long getExternalLinks() {
		return externalLinks;
	}

	public float getMozRank() {
		return mozRank;
	}

	public float getMozTrust() {
		return mozTrust;
	}

	public boolean ping(int timeout) {
		try {
			return InetAddress.getByName(url.getHost()).isReachable(timeout);
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Domain domain = (Domain) o;
		return linkingRootDomains == domain.linkingRootDomains &&
				externalLinks == domain.externalLinks &&
				Float.compare(domain.mozRank, mozRank) == 0 &&
				Float.compare(domain.mozTrust, mozTrust) == 0 &&
				Objects.equals(getUri(), domain.getUri());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUri(), linkingRootDomains, externalLinks, mozRank, mozTrust);
	}

	@Override
	public String toString() {
		return "Domain{" +
				"domain=" + url +
				", linkingRootDomains=" + linkingRootDomains +
				", externalLinks=" + externalLinks +
				", mozRank=" + mozRank +
				", mozTrust=" + mozTrust +
				'}';
	}
}
