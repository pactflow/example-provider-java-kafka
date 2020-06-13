package io.pactflow.example.kafka;

import org.springframework.messaging.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;

public class ProductMessageBuilder {
  private ObjectMapper mapper = new ObjectMapper();
  private ProductEvent product;

  public ProductMessageBuilder withProduct(ProductEvent product) {
    this.product = product;
    return this;
  }

  public Message<String> build() throws JsonProcessingException {
    final Message<String> message = MessageBuilder.withPayload(this.mapper.writeValueAsString(this.product))
        .setHeader(KafkaHeaders.TOPIC, "products")
        // .setHeader(KafkaHeaders.MESSAGE_KEY, "999")
        // .setHeader(KafkaHeaders.PARTITION_ID, 0)
        // .setHeader("X-Custom-Header", "Sending Custom Header with Spring Kafka")
        .build();

    return message;
  }

}