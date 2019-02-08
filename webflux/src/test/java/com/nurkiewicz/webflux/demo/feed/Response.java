package com.nurkiewicz.webflux.demo.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Response {
    private final List<Blog> bloggers;

    @JsonCreator
    public Response(@JsonProperty("bloggers") List<Blog> bloggers) {
        this.bloggers = bloggers;
    }

    public List<Blog> getBloggers() {
        return bloggers;
    }
}
