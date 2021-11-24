package com.nurkiewicz.reactor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.nurkiewicz.reactor.samples.Sleeper;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;

import static reactor.core.scheduler.Schedulers.newBoundedElastic;

@Ignore
public class R080_Backpressure {

    private static final Logger log = LoggerFactory.getLogger(R080_Backpressure.class);

    @Test
    public void whatIsBackpressure() throws Exception {
        //given
        Hooks.onOperatorDebug();
        final Flux<Long> flux = Flux
                .interval(Duration.ofMillis(10))
                .doOnNext(x -> log.info("Emitting {}", x))
                .onBackpressureError()
                .onBackpressureDrop(x -> log.warn("Dropped {}", x))
                .publishOn(newBoundedElastic(10 , 10, "Two"));

        //when
        flux.subscribe(x -> {
                    log.info("Handling {}", x);
                    Sleeper.sleep(Duration.ofMillis(1110));
                },
                e -> {
                    log.error("Opps", e);
                }
        );

        //then
        TimeUnit.SECONDS.sleep(50);
    }
}
