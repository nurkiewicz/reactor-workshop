package com.nurkiewicz.webflux.demo.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Blog {
    private final String name;
    private final URL rss;

    @JsonCreator
    public Blog(@JsonProperty("name") String name, @JsonProperty("rss") URL rss) {
        this.name = name;
        this.rss = rss;
    }

    @Override
    public String toString() {
        return "<outline type=\"rss\" title=\"" + name + "\" xmlUrl=\"" + rss + "\"/>";
    }
}
