package com.nurkiewicz.reactor.samples;

public class Weather {

	private final double temperature;

	public Weather(double temperature) {
		this.temperature = temperature;
	}

	public double getTemperature() {
		return temperature;
	}

	@Override
	public String toString() {
		return "Weather{temperature=" + temperature + '}';
	}
}
