package com.nurkiewicz.webflux.demo.security;

import java.util.StringJoiner;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/secure", produces = APPLICATION_JSON_VALUE)
class SecuredController {

	private final SecretService secretService;

	SecuredController(SecretService secretService) {
		this.secretService = secretService;
	}

	/**
	 * Can't return Flux of String: https://twitter.com/sdeleuze/status/956136517348610048
	 */
	@GetMapping(value = "/alpha")
	Flux<Secret> user() {
		return secretService.userSecrets();
	}

	@GetMapping(value = "/beta")
	Flux<Secret> admin() {
		return secretService.adminsSecrets();
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

	@Override
	public String toString() {
		return new StringJoiner(", ", Secret.class.getSimpleName() + "[", "]")
				.add("key='" + key + "'")
				.add("value='" + value + "'")
				.toString();
	}
}