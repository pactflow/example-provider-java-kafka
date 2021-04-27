package io.pactflow.example.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

@Component
public class DummyEventGenerator {
	private static final Logger log = LoggerFactory.getLogger(DummyEventGenerator.class);

	Faker faker = new Faker();

	@Autowired
	private ProductRepository repository;

	@Scheduled(fixedRate = 3000)
	public void generateProductEvent() {
		log.info("SEND_TEST_EVENTS {}", System.getenv("SEND_TEST_EVENTS"));
		if (!System.getenv("SEND_TEST_EVENTS").toLowerCase().equals("false")) {
			final ProductEvent event = new ProductEvent(faker.internet().uuid(),
					faker.commerce().productName(),
					faker.commerce().material(), "v1", faker.options().option(EventType.class), Double.parseDouble(faker.commerce().price()));

			log.info("sending random product event to stream: {}", event);
			repository.save(event);
		}
	}
}