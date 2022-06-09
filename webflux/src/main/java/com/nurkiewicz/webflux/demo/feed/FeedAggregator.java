package com.nurkiewicz.webflux.demo.feed;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

import com.rometools.rome.io.FeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.springframework.stereotype.Component;

@Component
public class FeedAggregator {

    private static final Logger log = LoggerFactory.getLogger(FeedAggregator.class);

    private final BlogsReader blogsReader;
    private final ArticlesReader articlesReader;

    public FeedAggregator(BlogsReader blogsReader, ArticlesReader articlesReader) {
        this.blogsReader = blogsReader;
        this.articlesReader = articlesReader;
    }

    /**
     * TODO (4) Read all feeds and store them into database using {@link ArticleRepository}
     * TODO (5) Repeat periodically, do not store duplicates. Hint: use {@link ArticleRepository#existsById(Object)}
     */
//    @PostConstruct
    public void init() throws IOException, FeedException, SAXException, ParserConfigurationException {
        final String feed = blogsReader.allBlogs().get(0).getXmlUrl();
        articlesReader.loadFromOneBlog(new URL(feed)).forEach(e -> {
            log.info("{}: {} at {}", e.getPublishedDate(), e.getTitle(), e.getLink());
        });
    }
}
