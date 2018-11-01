package com.nurkiewicz.reactor.samples;

import reactor.core.publisher.Flux;

public class WeatherService {

	public static Flux<Weather> measurements() {
		return Flux.just(
				new Weather(14.0),
				new Weather(14.0),
				new Weather(14.1),
				new Weather(14.2),
				new Weather(14.3),
				new Weather(14.4),
				new Weather(14.5),
				new Weather(14.6),
				new Weather(14.7),
				new Weather(16.0),
				new Weather(15.7),
				new Weather(15.2),
				new Weather(14.0)
		);
	}

}
