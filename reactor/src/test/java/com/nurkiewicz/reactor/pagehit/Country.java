package com.nurkiewicz.reactor.pagehit;


import java.util.concurrent.ThreadLocalRandom;

public enum Country {
    PL, US, RO;

    static Country random() {
        double rand = ThreadLocalRandom.current().nextDouble();
        if (rand < 0.5) {
            return PL;
        }
        if (rand < 0.99) {
            return US;
        }
        return RO;
    }
}