package io.nullptr.cmb.controller;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @CrossOrigin
    @PutMapping("/{productCode}/subscribe")
    public Map<String, String> subscribeProduct(@PathVariable String productCode) {
        Optional<Product> product = productRepository.findByInnerCode(productCode);

        if (product.isPresent()) {
            product.get().setSubscribed(true);
            productRepository.save(product.get());
        }

        return Map.of("code", "200");
    }

    @CrossOrigin
    @PutMapping("/{productCode}/unsubscribe")
    public Map<String, String> unsubscribeProduct(@PathVariable String productCode) {
        Optional<Product> product = productRepository.findByInnerCode(productCode);

        if (product.isPresent()) {
            product.get().setSubscribed(false);
            productRepository.save(product.get());
        }

        return Map.of("code", "200");
    }
}
