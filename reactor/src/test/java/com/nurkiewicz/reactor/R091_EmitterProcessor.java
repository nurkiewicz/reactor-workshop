package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

import static com.nurkiewicz.reactor.R090_UnicastProcessor.pushSomeEvents;

@Ignore
public class R091_EmitterProcessor {

    private static final Logger log = LoggerFactory.getLogger(R091_EmitterProcessor.class);

    @Test(timeout = 2_000)
    public void blockOnOverflow() throws Exception {
        //given
        final EmitterProcessor<Long> proc = EmitterProcessor.create(10);

        //when
        pushSomeEvents(proc, 0, 1000);

        //then
    }

    @Test
    public void multipleSubscribers() throws Exception {
        //given
        final EmitterProcessor<Long> proc = EmitterProcessor.create(10);

        //when
        pushSomeEvents(proc, 0, 3);

        //then
        proc
                .publishOn(Schedulers.elastic())
                .subscribe(
                        x -> log.info("A: Got {}", x),
                        e -> log.error("A: Error", e));
        proc
                .publishOn(Schedulers.elastic())
                .subscribe(
                        x -> log.info("B: Got {}", x),
                        e -> log.error("B: Error", e));

        pushSomeEvents(proc, 10, 3);
        TimeUnit.SECONDS.sleep(1);
    }

}
