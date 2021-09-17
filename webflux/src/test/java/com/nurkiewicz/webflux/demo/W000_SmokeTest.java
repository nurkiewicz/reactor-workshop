package com.nurkiewicz.webflux.demo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class W000_SmokeTest {

	@BeforeClass
	public static void init() {
		InitDocker.start();
	}

	@Test
	public void contextLoads() {
	}

}
