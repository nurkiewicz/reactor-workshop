package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.data.couchbase.repository.ReactiveCouchbaseSortingRepository;

public interface EmojiRepository extends ReactiveCouchbaseSortingRepository<Emoji, String> {}
