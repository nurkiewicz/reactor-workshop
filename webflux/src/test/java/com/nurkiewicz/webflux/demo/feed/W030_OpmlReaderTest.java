package com.nurkiewicz.webflux.demo.feed;

import java.util.List;

import com.rometools.opml.feed.opml.Outline;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO (1) Return {@link OpmlReader#allFeeds()} as {@link Flux} in {@link OpmlReader#allFeedsStream()}, lazily
 */
@Ignore
public class W030_OpmlReaderTest {

	@Test
	public void shouldReturnStreamOfBlogs() {
		//given
		OpmlReader opmlReader = new OpmlReader("/feed-en.xml");

		//when
		Flux<Outline> stream = opmlReader.allFeedsStream();

		//then
		List<String> blogs = stream
				.map(Outline::getTitle)
				.collectList().block();
		assertThat(blogs)
				.contains(
						"Javalobby - The heart of the Java developer community",
						"High Scalability",
						"Google Testing Blog",
						"The Netflix Tech Blog",
						"Hacker News Best",
						"NoBlogDefFound")
				.hasSize(17);
	}

	@Test
	public void shouldBeLazy() {
		//given
		OpmlReader opmlReader = new OpmlReader("/feed-404.xml");

		//when
		opmlReader.allFeedsStream();

		//then
		//no exception thrown
	}


}