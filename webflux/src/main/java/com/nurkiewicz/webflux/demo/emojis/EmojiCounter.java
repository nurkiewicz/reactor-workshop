package com.nurkiewicz.webflux.demo.emojis;

import org.springframework.stereotype.Component;

@Component
class EmojiCounter {

    private final EmojiRepository repository;

    EmojiCounter(EmojiRepository repository) {
        this.repository = repository;
    }


}
