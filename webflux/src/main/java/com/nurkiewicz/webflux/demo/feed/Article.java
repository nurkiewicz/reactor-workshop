package com.nurkiewicz.webflux.demo.feed;

import org.springframework.data.annotation.Id;

import java.net.URI;
import java.time.Instant;

public class Article {

    @Id
    private final URI link;

    private final Instant publishedDate;
    private final String title;

    public Article(URI link, Instant publishedDate, String title) {
        this.link = link;
        this.publishedDate = publishedDate;
        this.title = title;
    }

    public URI getLink() {
        return link;
    }

    public Instant getPublishedDate() {
        return publishedDate;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Article{" +
                "link=" + link +
                ", publishedDate=" + publishedDate +
                ", title='" + title + '\'' +
                '}';
    }
}
