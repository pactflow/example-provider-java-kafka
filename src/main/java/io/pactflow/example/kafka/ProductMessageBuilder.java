package io.pactflow.example.kafka;

import org.springframework.messaging.Message;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;

public class ProductMessageBuilder {
  private ObjectMapper mapper = new ObjectMapper();
  private ProductEvent product;

  public ProductMessageBuilder withProduct(ProductEvent product) {
    this.product = product;
    return this;
  }

  public Message<String> build() throws JacksonException {
    return MessageBuilder.withPayload(this.mapper.writeValueAsString(this.product))
        .setHeader(KafkaHeaders.TOPIC, "products").setHeader("Content-Type", "application/json; charset=utf-8")
        .build();
  }

}