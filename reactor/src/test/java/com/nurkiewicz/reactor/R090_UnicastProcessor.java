package com.nurkiewicz.reactor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

@Ignore
public class R090_UnicastProcessor {

    private static final Logger log = LoggerFactory.getLogger(R090_UnicastProcessor.class);

    @Test
    public void overflow() throws Exception {
        //given
        final UnicastProcessor<Long> proc = UnicastProcessor
                .create(
                        new ArrayBlockingQueue<>(10),
                        x -> log.warn("Dropped {}", x),
                        () -> {
                        });

        //when
        pushSomeEvents(proc, 0, 11);

        //then
    }

    @Test
    public void overflowBeforeSubscribing() throws Exception {
        //given
        final UnicastProcessor<Long> proc = UnicastProcessor
                .create(
                        new ArrayBlockingQueue<>(10),
                        x -> log.warn("Dropped {}", x),
                        () -> {
                        });


        //when
        pushSomeEvents(proc, 0, 11);

        //then
        proc.subscribe(
                x -> log.info("Got {}", x),
                e -> log.error("Error", e));
    }

    @Test
    public void twoSubscribers() throws Exception {
        //given
        final UnicastProcessor<Long> proc = UnicastProcessor
                .create(
                        new ArrayBlockingQueue<>(10),
                        x -> log.warn("Dropped {}", x),
                        () -> {
                        });


        //when
        pushSomeEvents(proc, 0, 11);

        //then
        proc
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        x -> log.info("Got {}", x),
                        e -> log.error("Error", e));
        proc
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        x -> log.info("Got {}", x),
                        e -> log.error("Error", e));

        TimeUnit.SECONDS.sleep(1);
    }

    static void pushSomeEvents(Processor<Long, Long> proc, int start, int count) {
        LongStream
                .range(start, start + count)
                .peek(x -> log.info("Pushing {}", x))
                .forEach(proc::onNext);
    }
}
