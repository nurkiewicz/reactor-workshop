package com.nurkiewicz.webflux.demo.emojis;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
class Emoji {

	@Id
	private String raw;

	@Field
	private long count;

}