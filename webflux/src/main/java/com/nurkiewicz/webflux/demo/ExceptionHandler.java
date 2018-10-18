package com.nurkiewicz.webflux.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
class ExceptionHandler extends DefaultErrorAttributes {

	private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
		Map<String, Object> attributes = super.getErrorAttributes(request, includeStackTrace);
		log.error("Returning error to user: {}", attributes, getError(request));
		return attributes;
	}
}
