package com.nurkiewicz.webflux.demo;

import java.time.Duration;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock
public abstract class IntegrationTest {

	@BeforeClass
	public static void init() {
		InitDocker.start().block(Duration.ofMinutes(2));
	}

}
