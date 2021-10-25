package com.nurkiewicz.reactor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.nurkiewicz.reactor.domains.Domain;
import com.nurkiewicz.reactor.domains.Domains;
import com.nurkiewicz.reactor.domains.Tld;
import com.nurkiewicz.reactor.user.LoremIpsum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static reactor.util.function.Tuples.of;

public class R061_GroupBy {

    private static final Logger log = LoggerFactory.getLogger(R061_GroupBy.class);

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
     * If it was SQL:
     *
     * <code>
     *   SELECT d.tld, SUM(linking_root_domains) AS s
     *   FROM domains d
     *   GROUP BY d.tld
     *   ORDER BY s DESC
     * </code>
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
        final Flux<Tuple2<Tld, Long>> tldToTotalLinkingRootDomains = domains
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
                .expectNext(of(new Tld("com"), 87_760_745L))
                .expectNext(of(new Tld("org"), 9_041_936L))
                .expectNext(of(new Tld("gov"), 2_331_526L))
                .expectNext(of(new Tld("uk"), 1_922_036L))
                .expectNext(of(new Tld("jp"), 1_869_929L))
                .expectNext(of(new Tld("net"), 1_627_997L))
                .expectNext(of(new Tld("edu"), 1_490_589L))
                .expectNext(of(new Tld("be"), 1_076_391L))
                .expectNext(of(new Tld("de"), 1_056_153L))
                .expectNext(of(new Tld("cn"), 1_051_999L))
                .expectNext(of(new Tld("gl"), 995_299L))
                .expectNext(of(new Tld("ru"), 917_921L))
                .expectNext(of(new Tld("ly"), 738_098L))
                .expectNext(of(new Tld("co"), 533_094L))
                .expectNext(of(new Tld("eu"), 454_617L))
                .expectNext(of(new Tld("me"), 424_173L))
                .expectNext(of(new Tld("fr"), 404_814L))
                .expectNext(of(new Tld("ca"), 233_912L))
                .expectNext(of(new Tld("es"), 229_458L))
                .expectNext(of(new Tld("nl"), 204_242L))
                .expectNext(of(new Tld("to"), 181_817L))
                .expectNext(of(new Tld("la"), 177_604L))
                .expectNext(of(new Tld("pl"), 171_095L))
                .expectNext(of(new Tld("br"), 158_381L))
                .expectNext(of(new Tld("it"), 150_409L))
                .expectNext(of(new Tld("us"), 142_895L))
                .expectNext(of(new Tld("au"), 139_500L))
                .expectNext(of(new Tld("io"), 124_859L))
                .expectNext(of(new Tld("tv"), 105_500L))
                .expectNext(of(new Tld("int"), 104_755L))
                .expectNext(of(new Tld("ch"), 90_364L))
                .expectNext(of(new Tld("cz"), 87_073L))
                .expectNext(of(new Tld("in"), 55_601L))
                .expectNext(of(new Tld("se"), 55_181L))
                .expectNext(of(new Tld("info"), 37_701L))
                .expectNext(of(new Tld("no"), 35_631L))
                .verifyComplete();
    }

    private long sum(List<Long> list) {
        return list
                .stream()
                .mapToLong(x -> x)
                .sum();
    }

}
