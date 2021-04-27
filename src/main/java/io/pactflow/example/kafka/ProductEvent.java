package io.pactflow.example.kafka;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;



@Data
class ProductEvent {
  @JsonFormat( shape = JsonFormat.Shape.STRING)
  private String id;
  private String name;
  private String type;
  private String version;
  private Double price;
  private EventType event;

  ProductEvent() {}

  ProductEvent(String id, String name, String type, String version, EventType event, Double price) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.version = version;
    this.event = event;
    this.price = price;
  }
}