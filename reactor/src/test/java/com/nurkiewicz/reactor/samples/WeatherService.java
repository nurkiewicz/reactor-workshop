package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Flux;

public class WeatherService {

	public static Flux<Weather> measurements() {
		return Flux
				.just(14.0, 14.0, 14.1, 14.2, 14.3, 14.4, 14.5, 14.6, 14.7, 16.0, 15.7, 15.2, 14.0)
				.map(Weather::new);
	}

}
