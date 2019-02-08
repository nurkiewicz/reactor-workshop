package com.nurkiewicz.reactor.realestate;

import reactor.core.publisher.Flux;

public class HouseSnapshots {

    private final HouseApi houseApi;

    public HouseSnapshots(HouseApi houseApi) {
        this.houseApi = houseApi;
    }

    /**
     * TODO Implement, make sure it's lazy
     */
    public Flux<HouseSnapshot> stream() {
        return Flux.empty();
    }

}
