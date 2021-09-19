package com.nurkiewicz.webflux.demo.security;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
class R2dbcConfig {

	@Bean
	public org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer  initializer(ConnectionFactory connectionFactory) {
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new CompositeDatabasePopulator(
				new ResourceDatabasePopulator(new ClassPathResource("schema.sql")),
				new ResourceDatabasePopulator(new ClassPathResource("data.sql"))));
		return initializer;
	}

}
