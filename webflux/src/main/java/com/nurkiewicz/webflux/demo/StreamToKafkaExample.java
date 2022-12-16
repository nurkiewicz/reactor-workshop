package com.nurkiewicz.webflux.demo;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

public class StreamToKafkaExample {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String TOPIC = "mastodon_posts";
	public static final String SERVER = "localhost:9092";

	public static void main(String[] args) throws UnknownHostException {
		Flux<ConsumerRecords<String, String>> incoming = Flux.generate(
				() -> {
					KafkaConsumer<String, String> consumer = createConsumer();
					consumer.subscribe(List.of(TOPIC));
					return consumer;
				}, (consumer, sink) -> {
					sink.next(consumer.poll(Duration.ofMillis(100)));
					return consumer;
				}, KafkaConsumer::close);
		incoming
				.doOnNext(s -> log.info("Got here: {}", s))
				.blockLast();
//		produce();
//		consume();
	}

	private static void consume() throws UnknownHostException {
		try (KafkaConsumer<String, String> consumer = createConsumer()) {
			consumer.subscribe(List.of(TOPIC));
			log.info("Consuming...");
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> msg : records) {
					log.info("Got {}", msg);
				}

			}
		}
	}

	@NotNull
	private static KafkaConsumer<String, String> createConsumer() throws UnknownHostException {
		Properties config = new Properties();
		config.put("group.id", InetAddress.getLocalHost().getHostName() + "c");
		config.put("bootstrap.servers", SERVER);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

		KafkaConsumer<String, String> stringStringKafkaConsumer = new KafkaConsumer<>(config);
		return stringStringKafkaConsumer;
	}

	private static void produce() throws UnknownHostException {
		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", SERVER);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		try (KafkaProducer<String, String> producer = new KafkaProducer<>(config)) {
			WebClient.create()
					.get()
					.uri("https://mastodon.social/api/v1/streaming/public")
					.retrieve()
					.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
					})
					.filter(e -> e.event() != null && e.data() != null)
					.take(100)
					.flatMap(event -> send(producer, new ProducerRecord<>(TOPIC, event.event(), event.data())))
					.doOnNext(e -> log.info("Sent {}", e))
					.blockLast();
		}
	}

	private static Mono<RecordMetadata> send(KafkaProducer<String, String> producer, ProducerRecord<String, String> msg) {
		return Mono.defer(() -> {
			Sinks.One<RecordMetadata> sink = Sinks.one();
			producer.send(msg, (metadata, e) -> {
				if (e == null) {
					sink.tryEmitValue(metadata);
				} else {
					sink.tryEmitError(e);
				}
			});
			return sink.asMono();
		});
	}

}
