package io.pactflow.example.kafka;

import tools.jackson.core.JacksonException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
class ProductRepository {
  @Autowired
  private KafkaTemplate<String, String> template;
  public static Logger logger = LoggerFactory.getLogger(Application.class);

  public void save(final ProductEvent product) {
    logger.info("writing product to stream", product);

    try {
      Message<String> message = new ProductMessageBuilder().withProduct(product).build();
      this.template.send(message);

    } catch (final JacksonException e) {
      logger.error("unable to serialise product to JSON", e);
    }
  }
}