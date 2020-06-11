package com.example.springboot;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping(value = "/", produces = "application/json; charset=utf-8")
class ProductController {

  private final ProductRepository repository;

  ProductController(ProductRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/products")
  List<Product> all() {
    return repository.findAll();
  }

  @PostMapping("/products")
  Product newProduct(@RequestBody Product newProduct) {
    return repository.save(newProduct);
  }

  @GetMapping({ "/product/{id}" })
  Product one(@PathVariable Long id) {

    return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
  }

  @PutMapping({ "/product/{id}" })
  Product replaceProduct(@RequestBody Product newProduct, @PathVariable Long id) {

    return repository.findById(id).map(Product -> {
      Product.setName(newProduct.getName());
      Product.setType(newProduct.getType());
      return repository.save(Product);
    }).orElseGet(() -> {
      newProduct.setId(id);
      return repository.save(newProduct);
    });
  }

  @DeleteMapping({ "/product/{id}" })
  void deleteProduct(@PathVariable Long id) {
    repository.deleteById(id);
  }
}