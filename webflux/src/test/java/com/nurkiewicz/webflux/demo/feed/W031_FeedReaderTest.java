package com.nurkiewicz.webflux.demo.feed;

import java.net.MalformedURLException;
import java.time.Duration;

import com.nurkiewicz.webflux.demo.InitDocker;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class W031_FeedReaderTest {

	@BeforeClass
	public static void init() {
		InitDocker.start();
	}

	@Autowired
	private FeedReader feedReader;

	@Test
	public void shouldDownloadFromUrl() throws MalformedURLException {
		String html = feedReader
				.getAsync("http://www.example.com")
				.block(Duration.ofSeconds(5));
		System.out.println(html);
	}


}

