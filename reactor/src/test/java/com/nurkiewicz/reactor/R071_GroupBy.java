package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.domains.Domain;
import com.nurkiewicz.reactor.domains.Domains;
import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static reactor.util.function.Tuples.of;

public class R071_GroupBy {

    private static final Logger log = LoggerFactory.getLogger(R071_GroupBy.class);

    @Test
    public void groupWordsByLength() throws Exception {
        //given
        final Flux<GroupedFlux<Integer, String>> wordsByLength = LoremIpsum
                .wordStream()
                .groupBy(String::length);

        //when
        final Flux<Long> numOfWords = wordsByLength
                .flatMap((GroupedFlux<Integer, String> words) -> words.count());

        final Flux<Integer> wordLengths = wordsByLength
                .map((GroupedFlux<Integer, String> words) -> words.key());

        //then
        final List<Integer> uniqueWordLengths = wordLengths.collectList().block();
        assertThat(uniqueWordLengths).contains(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12);

        final Set<Long> numOfWordsSet = numOfWords.collect(toSet()).block();
        assertThat(numOfWordsSet).contains(1L, 2L, 3L, 6L, 8L, 9L, 10L, 14L, 15L, 24L, 25L);

        wordsByLength
                .subscribe(words -> words.collectList()
                        .subscribe(list -> log.info("Words of length {}: {}", words.key(), list)));
    }

    /**
     * TODO Count number of words of each length.
     * Use {@link Flux#groupBy(Function)}
     */
    @Test
    public void countAndNumberOfWords() throws Exception {
        final Flux<GroupedFlux<Integer, String>> wordsByLength = LoremIpsum
                .wordStream()
                .groupBy(String::length);

        //when
        final Flux<Tuple2<Integer, Long>> lenToCount = wordsByLength
                .flatMap((GroupedFlux<Integer, String> words) -> words
                        .count()
                        .map(c -> of(words.key(), c))
                );

        //then
        final Set<Tuple2<Integer, Long>> pairs = lenToCount
                .collect(toSet())
                .block();

        assertThat(pairs)
                .containsOnly(
                        of(1, 1L),
                        of(2, 8L),
                        of(3, 15L),
                        of(4, 24L),
                        of(5, 25L),
                        of(6, 14L),
                        of(7, 10L),
                        of(8, 6L),
                        of(9, 9L),
                        of(11, 3L),
                        of(12, 2L)
                );
    }

    /**
     * TODO Count total number of linking root domains ({@link Domain#getLinkingRootDomains()}) to each TLD ({@link Domain#getTld()}
     * Sort from most to least number of linking root domains.
     *
     * @see Domain#getTld()
     * @see Domain#getLinkingRootDomains()
     * @see Flux#groupBy(Function)
     * @see Flux#collectList()
     * @see Flux#sort(Comparator)
     * @see Comparator#comparing(Function)
     */
    @Test
    public void countDomainsInTld() throws Exception {
        //given
        final Flux<Domain> domains = Domains.all();

        //when
        final Flux<Tuple2<String, Long>> tldToTotalLinkingRootDomains = domains
                .groupBy(Domain::getTld)
                .flatMap(tlds -> tlds
                        .map(Domain::getLinkingRootDomains)
                        .collectList()
                        .map(this::sum)
                        .map(total -> of(tlds.key(), total)))
                .sort(Comparator.comparing(t -> -t.getT2()));

        //then
        tldToTotalLinkingRootDomains
                .as(StepVerifier::create)
                .expectNext(of("com", 87760745L))
                .expectNext(of("org", 9041936L))
                .expectNext(of("gov", 2331526L))
                .expectNext(of("uk", 1922036L))
                .expectNext(of("jp", 1869929L))
                .expectNext(of("net", 1627997L))
                .expectNext(of("edu", 1490589L))
                .expectNext(of("be", 1076391L))
                .expectNext(of("de", 1056153L))
                .expectNext(of("cn", 1051999L))
                .expectNext(of("gl", 995299L))
                .expectNext(of("ru", 917921L))
                .expectNext(of("ly", 738098L))
                .expectNext(of("co", 533094L))
                .expectNext(of("eu", 454617L))
                .expectNext(of("me", 424173L))
                .expectNext(of("fr", 404814L))
                .expectNext(of("ca", 233912L))
                .expectNext(of("es", 229458L))
                .expectNext(of("nl", 204242L))
                .expectNext(of("to", 181817L))
                .expectNext(of("la", 177604L))
                .expectNext(of("pl", 171095L))
                .expectNext(of("br", 158381L))
                .expectNext(of("it", 150409L))
                .expectNext(of("us", 142895L))
                .expectNext(of("au", 139500L))
                .expectNext(of("io", 124859L))
                .expectNext(of("tv", 105500L))
                .expectNext(of("int", 104755L))
                .expectNext(of("ch", 90364L))
                .expectNext(of("cz", 87073L))
                .expectNext(of("in", 55601L))
                .expectNext(of("se", 55181L))
                .expectNext(of("info", 37701L))
                .expectNext(of("no", 35631L))
                .verifyComplete();
    }

    private long sum(List<Long> list) {
        return list
                .stream()
                .mapToLong(x -> x)
                .sum();
    }

}
