package com.nurkiewicz.reactor.realestate;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

public class HouseApi {

    /**
     * Returns the state of {@link HouseSnapshot} at given timestamp as well as last modification timestamp.
     * If you want to ask for previous version, use last modification timestamp minus 1 millisecond.
     */
    public Mono<HouseSnapshot> fetchFor(Instant when) {
        return Mono.just(new HouseSnapshot(when.minusSeconds(100), BigDecimal.TEN));
    }

}
