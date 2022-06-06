package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.pagehit.Country;
import com.nurkiewicz.reactor.pagehit.PageHits;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@Ignore
public class R074_GroupingStreams {

    private static final Logger log = LoggerFactory.getLogger(R074_GroupingStreams.class);

    /**
     * TODO Start with {@link PageHits#random()}, first group by country,
     * then count how many hits per second.
     */
    @Test
    public void groupByCountryEverySecond() throws Exception {
        Flux<Tuple2<Country, Long>> hitsPerSecond = null; //TODO
    }

    /**
     * TODO Start with {@link PageHits#random()}, first group hits per second,
     * then count how many for each country.
     */
    @Test
    public void everySecondGroupByCountry() throws Exception {
        Flux<Tuple2<Country, Long>> hitsPerSecond = null; //TODO
    }

}
