package com.nurkiewicz.webflux.demo.feed;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class ArticlesReader {

	private static final Logger log = LoggerFactory.getLogger(ArticlesReader.class);

	private final WebClient webClient;

	public ArticlesReader(WebClient webClient) {
		this.webClient = webClient;
	}

	public Flux<SyndEntry> fetch(String url) {
		return getAsync(url)
				.doOnError(HttpClientErrorException.class, e -> log.warn("HTTP error when fetching {}: {}", url, e.toString()))
				.onErrorResume(HttpClientErrorException.class, e -> Mono.empty())
				.doOnError(UnknownHostException.class, e -> log.warn("Unknown host {}: {}", url, e.toString()))
				.onErrorResume(UnknownHostException.class, e -> Mono.empty())
				.doOnError(SSLHandshakeException.class, e -> log.warn("SSL error {}: {}", url, e.toString()))
				.onErrorResume(SSLHandshakeException.class, e -> Mono.empty())
				.doOnError(SocketException.class, e -> log.warn("Connection error {}: {}", url, e.toString()))
				.onErrorResume(SocketException.class, e -> Mono.empty())
				.flatMapIterable(feedBody -> parseFeed(url, feedBody))
				.doOnNext(syndEntry -> log.trace("Found entry: {}", syndEntry.getTitle()));
	}

	private Iterable<SyndEntry> parseFeed(String feedUrl, String feedBody) {
		try {
			log.debug("Parsing feed from {} ({} bytes)", feedUrl, feedBody.length());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream is = new ByteArrayInputStream(applyAtomNamespaceFix(feedBody).getBytes(UTF_8));
			Document doc = builder.parse(is);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(doc);
			return feed.getEntries();
		} catch (SAXParseException e) {
			log.warn("Unable to parse feed {} due to: {}", feedUrl, e.toString());
			return List.of();
		} catch (ParserConfigurationException | SAXException | IOException | FeedException e) {
			throw new RuntimeException(e);
		}
	}

	private String applyAtomNamespaceFix(String feedBody) {
		return feedBody.replace("https://www.w3.org/2005/Atom", "http://www.w3.org/2005/Atom");
	}

	Mono<String> getAsync(String url) {
		return webClient
				.get()
				.uri(url)
				.retrieve()
				.bodyToMono(String.class);
	}

}
