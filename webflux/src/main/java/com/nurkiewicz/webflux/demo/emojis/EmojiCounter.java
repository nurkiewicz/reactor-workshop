package com.nurkiewicz.webflux.demo.emojis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class EmojiCounter {

    private static final Logger log = LoggerFactory.getLogger(EmojiCounter.class);

    private final EmojiRepository repository;

    EmojiCounter(EmojiRepository repository) {
        this.repository = repository;
    }

}
