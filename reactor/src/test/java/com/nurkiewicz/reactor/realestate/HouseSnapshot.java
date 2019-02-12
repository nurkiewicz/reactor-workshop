package com.nurkiewicz.reactor.realestate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

public class HouseSnapshot {

    private final Instant since;
    private final BigDecimal price;

    public HouseSnapshot(Instant since, int price) {
        this(since, new BigDecimal(price));
    }

    public HouseSnapshot(Instant since, BigDecimal price) {
        this.since = since;
        this.price = price;
    }

    public Instant getSince() {
        return since;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HouseSnapshot.class.getSimpleName() + "[", "]")
                .add("since=" + since)
                .add("price=" + price)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HouseSnapshot)) return false;
        HouseSnapshot that = (HouseSnapshot) o;
        return Objects.equals(since, that.since) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(since, price);
    }
}
