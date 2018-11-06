package com.nurkiewicz.reactor.pagehit;

import com.devskiller.jfairy.Fairy;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class PageHits {

    public static Flux<PageHit> random() {
        final Fairy fairy = Fairy.create();
        return Flux
                .interval(Duration.ofMillis(2))
                .map(x -> PageHit.random(fairy));
    }

}
