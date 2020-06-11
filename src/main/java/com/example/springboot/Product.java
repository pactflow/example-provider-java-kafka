package com.example.springboot;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Entity
class Product {
  @JsonFormat( shape = JsonFormat.Shape.STRING)
  private @Id Long id;
  private String name;
  private String type;

  Product() {}

  Product(Long id, String name, String type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }
}