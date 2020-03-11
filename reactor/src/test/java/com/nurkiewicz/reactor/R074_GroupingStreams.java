package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.pagehit.Country;
import com.nurkiewicz.reactor.pagehit.PageHit;
import com.nurkiewicz.reactor.pagehit.PageHits;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;

public class R074_GroupingStreams {

    private static final Logger log = LoggerFactory.getLogger(R074_GroupingStreams.class);

    /**
     * TODO Start with {@link PageHits#random()}
     */
    @Test
    public void groupByCountryEverySecond() throws Exception {
        PageHits
                .random()
                .window(ofSeconds(1))
                .flatMap(oneSecond -> oneSecond
                        .groupBy(PageHit::getCountry)
                        .flatMap(byCountry -> count(byCountry.key(), byCountry))
                )
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * TODO Start with {@link PageHits#random()}
     */
    @Test
    public void everySecondGroupByCountry() throws Exception {
        PageHits
                .random()
                .groupBy(PageHit::getCountry)
                .flatMap(byCountry -> byCountry
                        .window(ofSeconds(1))
                        .flatMap(countryInOnSecond ->
                                count(byCountry.key(), countryInOnSecond)
                        )
                ).subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
    }

    private Mono<Tuple2<Country, Long>> count(Country key, Flux<PageHit> countryInOnSecond) {
        return countryInOnSecond
                .count()
                .map(cnt -> Tuples.of(key, cnt));
    }

}
