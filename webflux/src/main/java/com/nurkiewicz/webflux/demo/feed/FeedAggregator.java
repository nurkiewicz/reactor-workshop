package com.nurkiewicz.webflux.demo.feed;

import com.rometools.rome.io.FeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

@Component
public class FeedAggregator {

    private static final Logger log = LoggerFactory.getLogger(FeedAggregator.class);

    private final OpmlReader opmlReader;
    private final FeedReader feedReader;

    public FeedAggregator(OpmlReader opmlReader, FeedReader feedReader) {
        this.opmlReader = opmlReader;
        this.feedReader = feedReader;
    }

//    @PostConstruct
    public void init() throws IOException, FeedException, SAXException, ParserConfigurationException {
        final String feed = opmlReader.allFeeds().get(0).getXmlUrl();
        feedReader.fetch(new URL(feed)).forEach(e -> {
            log.info("{}: {} at {}", e.getPublishedDate(), e.getTitle(), e.getLink());
        });
    }
}
