package ua.spribe.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.spribe.store.model.Product;
import ua.spribe.store.service.ProductService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "products")
@RestController
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/buy")
    public void buyProducts(@RequestParam List<Long> productIds,
                            @RequestParam long customerId) {
        productService.buyProducts(productIds, customerId);
    }
}
