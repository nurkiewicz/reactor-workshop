package com.nurkiewicz.webflux.demo;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/secure", produces = APPLICATION_JSON_VALUE)
class SecuredController {

	/**
	 * Can't return Flux of String: https://twitter.com/sdeleuze/status/956136517348610048
	 */
	@GetMapping(value = "/secrets")
	Flux<Secret> secrets() {
		return Flux.just(
				new Secret("k1", "secret1"),
				new Secret("k2", "secret2"),
				new Secret("k3", "secret3")
		);
	}

}

class Secret{
	private final String key;
	private final String value;

	public Secret(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}