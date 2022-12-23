package com.apidemo.controllers;

import com.apidemo.database.Database;
import com.apidemo.models.Product;
import com.apidemo.models.ResponseObject;
import com.apidemo.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Query product success", product)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false", "Cannot find product", "")
                );
            }
        }  catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", e.getMessage(), "")
            );
        }
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody @Valid Product newProduct) {
        try {
            List<Product> listProducts = productRepository.findByNameProduct(newProduct.getNameProduct());
            if (listProducts.size() > 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false", "Name exits", "")
                );
            } else {
                productRepository.save(newProduct);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Query product success", newProduct)
                );
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", e.getMessage(), "")
            );
        }
    }

    @PutMapping("/update/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody @Valid Product newProduct, @PathVariable Long id){
        try {
            List<Product> listProducts = productRepository.findByNameProduct(newProduct.getNameProduct());
            if (listProducts.size() > 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false", "Name exits", "")
                );
            } else {
                Optional<Product> updateProduct = Optional.of(productRepository.findById(id)
                        .map(product -> {
                            product.setNameProduct(newProduct.getNameProduct());
                            product.setPrice(newProduct.getPrice());
                            product.setUrl(newProduct.getUrl());
                            return productRepository.save(product);
                        }).orElseGet(() -> {
                            newProduct.setId(id);
                            return productRepository.save(newProduct);
                        }));
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Query product success", newProduct)
                );
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", e.getMessage(), "")
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id){
        try {
            boolean checkProduct = productRepository.existsById(id);
            if (checkProduct) {
                productRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Query product success", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("false", "Product No Exits", "")
                );
            }
        }  catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", e.getMessage(), "")
            );
        }
    }
}
