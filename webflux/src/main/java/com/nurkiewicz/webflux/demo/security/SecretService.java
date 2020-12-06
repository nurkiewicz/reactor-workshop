package com.nurkiewicz.webflux.demo.security;

import reactor.core.publisher.Flux;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecretService {

	public Flux<Secret> userSecrets() {
		return generateSecrets(3, "alpha");
	}

	@PreAuthorize("hasRole('ADMIN')")
	public Flux<Secret> adminsSecrets() {
		return generateSecrets(2, "beta");
	}

	private Flux<Secret> generateSecrets(int count, String key) {
		return ReactiveSecurityContextHolder
				.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getPrincipal)
				.cast(User.class)
				.map(User::getUsername)
				.flatMapMany(user -> Flux.range(1, count)
						.map(i -> new Secret(key + "-" + i, user + "'s secret value #" + i))
				);
	}

}
