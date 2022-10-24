package com.nurkiewicz.reactor;

import com.nurkiewicz.reactor.realestate.HouseApi;
import com.nurkiewicz.reactor.realestate.HouseSnapshot;
import com.nurkiewicz.reactor.realestate.HouseSnapshots;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class R36_InfinitelyLazy {

    public static final Instant[] T = new Instant[]{
            Instant.ofEpochMilli(0),
            Instant.ofEpochMilli(1_000),
            Instant.ofEpochMilli(2_000)
    };

    public static final int[] P = new int[]{10, 9, 8};

    private HouseApi api;
    private HouseSnapshots snapshots;

    @Before
    public void setup() {
        api = mock(HouseApi.class);
        snapshots = new HouseSnapshots(api);
        mockApi();
    }

    private void mockApi() {
        when(api.fetchFor(T[2])).thenReturn(Mono.just(new HouseSnapshot(T[1], P[1])));
        when(api.fetchFor(T[1])).thenReturn(Mono.just(new HouseSnapshot(T[1], P[1])));
        when(api.fetchFor(T[1].minusMillis(1))).thenReturn(Mono.just(new HouseSnapshot(T[0], P[0])));
        when(api.fetchFor(T[0])).thenReturn(Mono.just(new HouseSnapshot(T[0], P[0])));
        when(api.fetchFor(T[0].minusMillis(1))).thenReturn(Mono.empty());
    }

    @Test
    @Ignore
    public void test_27() throws Exception {
        //given

        //then
        assertThat(api.fetchFor(T[2])).isEqualTo(new HouseSnapshot(T[1], P[1]));
        assertThat(api.fetchFor(T[1])).isEqualTo(new HouseSnapshot(T[1], P[1]));
        assertThat(api.fetchFor(T[1].minusMillis(1))).isEqualTo(new HouseSnapshot(T[0], P[0]));
        assertThat(api.fetchFor(T[0])).isEqualTo(new HouseSnapshot(T[0], P[0]));
        assertThat(api.fetchFor(T[0].minusMillis(1))).isNull();
    }

    @Test
    public void barelyCreatingStreamShouldNotTriggerAnyRequests() throws Exception {
        //given

        //when
        final Flux<HouseSnapshot> stream = snapshots.stream();

        //then
        assertThat(stream.collectList().block()).isEmpty();
    }

}
