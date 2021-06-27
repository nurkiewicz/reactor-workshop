package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.domains.Crawler;
import com.nurkiewicz.reactor.domains.Domain;
import com.nurkiewicz.reactor.domains.Domains;
import com.nurkiewicz.reactor.domains.Html;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static reactor.core.scheduler.Schedulers.elastic;

public class R054_Parallel {

	private static final Logger log = LoggerFactory.getLogger(R054_Parallel.class);

	/**
	 * TODO Crawl 500 domains as soon as possible using {@link Flux#parallel()} operator.
	 * Use {@link Crawler#crawlBlocking(Domain)}
	 *
	 * @see Flux#parallel(int)
	 */
	@Test(timeout = 10_000L)
	public void crawlConcurrently() throws Exception {
		//given
		final Flux<Domain> domains = Domains.all();

		//when
		final Flux<Html> htmls = domains
				.parallel(500)
				.runOn(elastic())
				.map(Crawler::crawlBlocking)
				.sequential();

		//then
		final List<String> strings = htmls.map(Html::getRaw).collectList().block();
		assertThat(strings)
				.hasSize(500)
				.contains("<html><title>http://mozilla.org</title></html>");
	}

	/**
	 * TODO Just like above, but return {@link Tuple2} with both {@link Domain} and {@link Html}
	 * <p>
	 * Use {@link Flux#parallel(int)} and no {@link Flux#flatMap(Function)}
	 * </p>
	 *
	 * @see Tuples#of(Object, Object)
	 */
	@Test(timeout = 10_000L)
	public void knowWhereHtmlCameFrom() throws Exception {
		//given
		final Flux<Domain> domains = Domains.all();

		//when
		final Flux<Tuple2<URI, Html>> tuples = domains
				.parallel(500)
				.runOn(elastic())
				.map(domain -> Tuples.of(domain.getUri(), Crawler.crawlBlocking(domain)))
				.sequential();

		//then
		final List<Tuple2<URI, Html>> list = tuples
				.collectList()
				.block();

		assertThat(list)
				.hasSize(500)
				.contains(Tuples.of(new URI("http://archive.org"), new Html("<html><title>http://archive.org</title></html>")))
				.contains(Tuples.of(new URI("http://github.com"), new Html("<html><title>http://github.com</title></html>")));

		list.forEach(pair ->
				assertThat(pair.getT2().getRaw()).contains(pair.getT1().getHost()));
	}

	/**
	 * TODO Just like above, but return {@link Mono} with {@link Map} inside.
	 * Key in that map should be a {@link URI} (why not {@link java.net.URL}?), a value is {@link Html}
	 *
	 * @see Flux#parallel(int)
	 */
	@Test(timeout = 10_000L)
	public void downloadAllAndConvertToJavaMap() throws Exception {
		//given
		final Flux<Domain> domains = Domains.all();

		//when
		final Mono<Map<URI, Html>> mapStream = domains
				.parallel(500)
				.runOn(elastic())
				.map(domain -> Tuples.of(domain.getUri(), Crawler.crawlBlocking(domain)))
				.sequential()
				.collectMap(Tuple2::getT1, Tuple2::getT2);

		//then
		final Map<URI, Html> map = mapStream.block();

		assertThat(map)
				.hasSize(500)
				.containsEntry(new URI("http://archive.org"), new Html("<html><title>http://archive.org</title></html>"))
				.containsEntry(new URI("http://github.com"), new Html("<html><title>http://github.com</title></html>"));

		map.forEach((key, value) ->
				assertThat(value.getRaw()).contains(key.getHost())
		);
	}

	/**
	 * TODO Copy-paste solution from above, but replace {@link Crawler#crawlBlocking(Domain)} with {@link Crawler#crawlThrottled(Domain)}.
	 * How to prevent {@link Flux#flatMap(Function)} from crawling too many domains at once?
	 *
	 * @see Flux#parallel(int)
	 */
	@Test(timeout = 20_000L)
	public void throttledDownload() throws Exception {
		//given
		final Flux<Domain> domains = Domains.all();

		//when
		final Mono<Map<URI, Html>> mapStream = domains
				.parallel(50)
				.runOn(elastic())
				.map(domain -> Tuples.of(domain.getUri(), Crawler.crawlThrottled(domain)))
				.sequential()
				.collectMap(Tuple2::getT1, Tuple2::getT2);

		//then
		final Map<URI, Html> map = mapStream.block();

		assertThat(map)
				.hasSize(500)
				.containsEntry(new URI("http://archive.org"), new Html("<html><title>http://archive.org</title></html>"))
				.containsEntry(new URI("http://github.com"), new Html("<html><title>http://github.com</title></html>"));

		map.forEach((key, value) ->
				assertThat(value.getRaw()).contains(key.getHost())
		);
	}

}
