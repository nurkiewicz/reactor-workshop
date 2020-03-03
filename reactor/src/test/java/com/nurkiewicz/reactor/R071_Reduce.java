package com.nurkiewicz.reactor;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.function.BiFunction;

@Ignore
public class R071_Reduce {

    private static final Logger log = LoggerFactory.getLogger(R071_Reduce.class);
    public static final String FACTORIAL_100 = "93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000";

    @Test
    public void sumUsingReduce() throws Exception {
        //given
        final Flux<Integer> nums = Flux.range(1, 10);

        //when
        final Mono<Integer> sum = nums.reduce((accumulator, element) -> {
            log.info("accumulator = {}, element = {}", accumulator, element);
            return accumulator + element;
        });

        //then
        sum
                .as(StepVerifier::create)
                .expectNext(
                        (((((((((1 + 2) + 3) + 4) + 5) + 6) + 7) + 8) + 9) + 10)
                )
                .verifyComplete();
    }

    /**
     * TODO Computer factorial (n!) using {@link Flux#reduce(BiFunction)}
     */
    @Test
    public void factorialUsingReduce() throws Exception {
        //given
        final Flux<Integer> nums = Flux.range(1, 10);

        //when
        final Mono<Integer> factorial = null;  //TODO

        //then
        factorial
                .as(StepVerifier::create)
                .expectNext(
                        (((((((((1 * 2) * 3) * 4) * 5) * 6) * 7) * 8) * 9) * 10)
                )
                .verifyComplete();
    }

    @Test
    public void reduceIsEmptyOnEmptyStream() throws Exception {
        //given
        final Flux<Integer> nums = Flux.empty();

        //when
        final Mono<Integer> sum = nums.reduce((accumulator, element) -> {
            log.info("accumulator = {}, element = {}", accumulator, element);
            return accumulator + element;
        });

        //then
        sum
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void reduceReturnsInitialValue() throws Exception {
        //given
        final Flux<Integer> nums = Flux.just(42);

        //when
        final Mono<Integer> sum = nums.reduce((accumulator, element) -> {
            log.info("accumulator = {}, element = {}", accumulator, element);
            return accumulator + element;
        });

        //then
        sum
                .as(StepVerifier::create)
                .expectNext(42)
                .verifyComplete();
    }

    @Test
    public void reduceWithCustomAccumulator() throws Exception {
        //given
        final Flux<Integer> nums = Flux.range(1, 100);

        //when
        final Mono<BigInteger> factorial = nums.reduce(BigInteger.ONE, (bi, x) -> {
            log.info("bi = {}, x = {}", bi, x);
            return bi.multiply(BigInteger.valueOf(x));
        });

        //then
        factorial
                .as(StepVerifier::create)
                .expectNext(new BigInteger(FACTORIAL_100))
                .verifyComplete();
    }


}
