package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.domains.Domain;
import com.nurkiewicz.reactor.domains.Domains;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

@Ignore
public class R26_ParsingCsv {

	private static final Logger log = LoggerFactory.getLogger(R26_ParsingCsv.class);

	/**
	 * TODO Implement {@link Domains#all()} so that tests pass
	 * @throws Exception
	 */
	@Test
	public void loadCsvAsStream() throws Exception {
		Domains.all()
				.as(StepVerifier::create)
				.expectNextCount(500)
				.verifyComplete();
	}

	@Test
	public void loadingTwice() throws Exception {
		//given
		final Flux<Domain> domains = Domains.all();

		//when
		final List<Domain> first = domains.collectList().block();
		final List<Domain> second = domains.collectList().block();

		//then
		assertThat(first).isEqualTo(second);
	}

	@Test
	public void fileHasCorrectData() throws Exception {
		//given

		//when
		final List<Domain> domains = Domains.all().collectList().block();

		//then
		assertThat(domains).hasSize(500);
		final Domain domain = domains.get(0);
		assertThat(domain.getUrl()).isEqualTo(new URL("http://facebook.com"));
		assertThat(domain.getLinkingRootDomains()).isEqualTo(14262527L);
		assertThat(domain.getExternalLinks()).isEqualTo(3737324796L);
		assertThat(domain.getMozRank()).isCloseTo(9.80f, offset(0.1f));
		assertThat(domain.getMozTrust()).isCloseTo(9.50f, offset(0.1f));
	}

}
