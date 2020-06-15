package io.pactflow.example.kafka;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit5.AmpqTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

// @Provider("AmqpProvider")
@Provider("pactflow-example-provider-java-kafka")
@PactBroker(scheme = "https", host = "${PACT_BROKER_HOST}", tags = { "master", "prod", "latest" }, authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}"))
@PactFolder("src/test/resources/amqp_pacts")
public class ProductsKafkaProducerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductsKafkaProducerTest.class);

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void testTemplate(Pact pact, Interaction interaction, PactVerificationContext context) {
    LOGGER.info("testTemplate called: " + pact.getProvider().getName() + ", " + interaction);
    context.verifyInteraction();
  }

  @BeforeEach
  void before(PactVerificationContext context) {
    context.setTarget(new AmpqTestTarget());

    System.out.println("TRAVIS_COMMIT" + System.getenv("TRAVIS_COMMIT"));
    System.setProperty("pact.provider.version",
        System.getenv("TRAVIS_COMMIT") == null ? "" : System.getenv("TRAVIS_COMMIT"));
    System.setProperty("pact.provider.tag",
        System.getenv("TRAVIS_BRANCH") == null ? "" : System.getenv("TRAVIS_BRANCH"));
    System.setProperty("pact.verifier.publishResults",
        System.getenv("PACT_BROKER_PUBLISH_VERIFICATION_RESULTS") == null ? "false" : "true");
  }

  @PactVerifyProvider("a product event update")
  public MessageAndMetadata productUpdateEvent() throws JsonProcessingException {
    ProductEvent product = new ProductEvent("id1", "product name", "product type", "v1", EventType.UPDATED);
    Message<String> message = new ProductMessageBuilder().withProduct(product).build();

    return generateMessageAndMetadata(message);
  }

  @PactVerifyProvider("a product created event")
  public MessageAndMetadata productCreatedEvent() throws JsonProcessingException {
    ProductEvent product = new ProductEvent("id1", "product name", "product type", "v1", EventType.CREATED);
    Message<String> message = new ProductMessageBuilder().withProduct(product).build();

    return generateMessageAndMetadata(message);
  }

  private MessageAndMetadata generateMessageAndMetadata(Message<String> message) {
    HashMap<String, Object> metadata = new HashMap<String, Object>();
    message.getHeaders().forEach((k, v) -> metadata.put(k, v));

    return new MessageAndMetadata(message.getPayload().getBytes(), metadata);
  }
}