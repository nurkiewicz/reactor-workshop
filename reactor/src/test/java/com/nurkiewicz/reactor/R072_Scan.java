package com.nurkiewicz.reactor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class R072_Scan {

    private static final Logger log = LoggerFactory.getLogger(R072_Scan.class);

    @Test
    public void sumUsingScan() throws Exception {
        //given
        final Flux<Integer> nums = Flux.range(1, 10);

        //when
        final Flux<Integer> sum = nums.scan((l, r) -> {
            log.info("l = {}, r = {}", l, r);
            return l + r;
        });

        //then
        sum
                .as(StepVerifier::create)
                .expectNext(1)
                .expectNext(1 + 2)
                .expectNext(3 + 3)
                .expectNext(6 + 4)
                .expectNext(10 + 5)
                .expectNext(15 + 6)
                .expectNext(21 + 7)
                .expectNext(28 + 8)
                .expectNext(36 + 9)
                .expectNext(45 + 10)
                .verifyComplete();
    }

    /**
     * TODO Compute running average from the beginning to current item.
     * E.g.: (10) / 1, (10, 20) / 2, (10, 20, 6) / 3, (10, 20, 6, 4) / 4, and so on.
     * Hint: use {@link Flux#scan(Object, BiFunction)} with an accumulator of type {@link Tuple2}
     * where one value is sum so far and the other is total number of items (needed to compute average).
     */
    @Test
    public void computeAverageUsingScan() throws Exception {
        //given
        final Flux<Integer> nums = Flux.just(10, 20, 6, 4, 20, 24);

        //when
        final Flux<Double> avg = nums
                .scan(Tuples.of(0, 0), (acc, cur) -> acc
                        .mapT1(x -> x + 1)
                        .mapT2(x -> x + cur)
                )
                .map(t -> (double)t.getT2() / t.getT1());

        //then
        avg
                .skip(1)
                .as(StepVerifier::create)
                .expectNext(10.0, 15.0, 12.0, 10.0, 12.0, 14.0)
                .verifyComplete();
    }
}

class BankOperation {
    private final Instant when;
    private final BigDecimal amount;

    BankOperation(Instant when, BigDecimal amount) {
        this.when = when;
        this.amount = amount;
    }

    public Instant getWhen() {
        return when;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

interface Bank {
    Flux<BankOperation> myAccount();

    default Mono<BigDecimal> balance() {
        return myAccount()
                .map(BankOperation::getAmount)
                .reduce(BigDecimal::add);
    }

    default Flux<BigDecimal> balanceHistory() {
        return myAccount()
                .map(BankOperation::getAmount)
                .scan(BigDecimal::add);
    }
}